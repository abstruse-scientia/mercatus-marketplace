package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.*;

import com.scientia.mercatus.exception.AmountMismatchException;
import com.scientia.mercatus.exception.OrderNotFoundException;

import com.scientia.mercatus.payment.PaymentGatewayRegistry;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.PaymentRepository;
import com.scientia.mercatus.service.IPaymentGateway;
import com.scientia.mercatus.service.IPaymentService;
import com.scientia.mercatus.util.CurrencyConversionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



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
                .orElseThrow(()-> new OrderNotFoundException("No order exists."));

        if(order.getOrderPaymentStatus() == OrderPaymentStatus.SUCCESS) {
            throw new IllegalStateException("Order already paid");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Payment cannot be initiated for this order");
        }

        long amountExpected = currencyConversion.toINRMinor(order.getTotalAmount());

        IPaymentGateway gateway = gatewayRegistry.get(provider);

        PaymentInitiationResultDto initiationResult = gateway.initiatePayment(
                orderReference,
                amountExpected,
                currency
        );

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
    public void markPaymentSuccess(PaymentProvider provider, String providerOrderId,
                                   String providerPaymentId, long amountReceived) {


        Payment payment = paymentPersistenceService.findPendingPayment(provider, providerOrderId);
        if (payment == null) {
            return; // idempotency : either duplicate or already processed
        }
        if (amountReceived != payment.getAmountExpected()) { // amount validation
            throw new AmountMismatchException("Amount mismatch. Expected: " + payment.getAmountExpected()
            +"Received: " + amountReceived);
        }

        //persist  success (tx1)
        String orderReference = paymentPersistenceService.persistPaymentSuccess(
                provider,
                providerOrderId,
                providerPaymentId,
                amountReceived
        );

        //order transition(tx2)
        paymentResultHandler.onPaymentSuccess(orderReference);
        paymentResultHandler.finalizePaidOrder(orderReference);
    }


    @Override
    public void markPaymentFailed(PaymentProvider provider, String providerOrderId,
                                  String providerPaymentId) {

        Payment payment = paymentPersistenceService.findPendingPayment(provider, providerOrderId);
        if (payment == null) {
            return;
        }

        //persist failure tx1
        String orderReference = paymentPersistenceService.persistPaymentFailure(
                provider,
                providerOrderId,
                providerPaymentId
        );



        //order transition tx2
        paymentResultHandler.onPaymentFailure(orderReference);
        paymentResultHandler.cancelFailedOrder(orderReference);



    }
}
