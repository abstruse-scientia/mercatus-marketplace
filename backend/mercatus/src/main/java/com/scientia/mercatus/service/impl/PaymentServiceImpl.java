package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.*;

import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;

import com.scientia.mercatus.payment.PaymentGatewayRegistry;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.PaymentRepository;
import com.scientia.mercatus.service.IPaymentGateway;
import com.scientia.mercatus.service.IPaymentService;
import com.scientia.mercatus.util.CurrencyConversionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {


    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGatewayRegistry gatewayRegistry;
    private final CurrencyConversionUtil currencyConversion;
    private final PaymentPersistenceService paymentPersistenceService;
    private final PaymentResultHandler paymentResultHandler;


    @Override
    @Transactional
    public PaymentInitiationResultDto initiatePayment(String orderReference,
                               String currency, PaymentProvider provider) {

        Order order = orderRepository
                .findByOrderReference(orderReference)
                .orElseThrow(()-> new BusinessException(ErrorEnum.ORDER_NOT_FOUND));

        if(order.getOrderPaymentStatus() == OrderPaymentStatus.SUCCESS) {
            throw new BusinessException(ErrorEnum.PAYMENT_ALREADY_EXISTS);
        }
        if (order.getOrderPaymentStatus() != OrderPaymentStatus.PENDING) {
            throw new BusinessException(ErrorEnum.INVALID_PAYMENT);
        }

        long amountExpected = currencyConversion.toINRMinor(order.getTotalAmount());

        IPaymentGateway gateway = gatewayRegistry.get(provider);

        PaymentInitiationResultDto initiationResult = gateway.initiatePayment(
                orderReference,
                amountExpected,
                currency
        );

        if (initiationResult == null) {
            throw new BusinessException(ErrorEnum.PAYMENT_GATEWAY_ERROR);
        }
        //Persist Payment Instance
        Payment payment = new Payment();
        payment.setOrderReference(orderReference);
        payment.setAmountExpected(amountExpected);
        payment.setCurrency(currency);
        payment.setProvider(provider);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProviderOrderId(initiationResult.orderId());
        payment.setProviderPaymentId(null);

        paymentRepository.save(payment);

        return initiationResult;

    }

    /*Scenario: Webhook Success */

    @Override
    @Transactional
    public void markPaymentSuccess(PaymentProvider provider, String providerOrderId,
                                   String providerPaymentId, long amountReceived) {


        Payment payment = findLatestPaymentRow(provider, providerOrderId);
        if (payment.getStatus() == PaymentStatus.SUCCESS) { // idempotency
            return;
        }
        if (payment.getStatus() == PaymentStatus.FAILED) {
            return; // out of order success -> ignore
        }
        if (amountReceived != payment.getAmountExpected()) { // amount validation
            log.info("Amount received: {} Expected amount: {}", amountReceived, payment.getAmountExpected());
            throw new BusinessException(ErrorEnum.AMOUNT_MISMATCH);
        }

        //persist  success (tx1)
        String orderReference = paymentPersistenceService.persistPaymentSuccess(
                provider,
                providerOrderId,
                providerPaymentId,
                amountReceived
        );

        //order transition(tx2)
        paymentResultHandler.handlePaymentSuccess(orderReference);
    }

    /*Scenario: Failure */
    @Override
    public void markPaymentFailed(PaymentProvider provider, String providerOrderId,
                                  String providerPaymentId) {

        Payment payment = findLatestPaymentRow(provider, providerOrderId);
        if (payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return; //ignore later failure
        }
        //persist failure tx1
        String orderReference = paymentPersistenceService.persistPaymentFailure(
                provider,
                providerOrderId,
                providerPaymentId
        );
        //order transition tx2
        paymentResultHandler.handlePaymentFailure(orderReference);

    }

    private Payment findLatestPaymentRow(PaymentProvider provider, String providerOrderId) {


        return paymentRepository.findFirstByProviderAndProviderOrderIdOrderByIdDesc(provider, providerOrderId)
                .orElseThrow(()-> new BusinessException(ErrorEnum.PAYMENT_NOT_FOUND));
    }
}
