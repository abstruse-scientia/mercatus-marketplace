package com.scientia.mercatus.service.impl;

import com.razorpay.RazorpayException;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.exception.InvalidPaymentStateException;
import com.scientia.mercatus.exception.OrderNotFoundException;
import com.scientia.mercatus.exception.PaymentAlreadyExistsException;
import com.scientia.mercatus.exception.PaymentNotFoundException;
import com.scientia.mercatus.payment.PaymentGatewayRegistry;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.PaymentRepository;
import com.scientia.mercatus.service.IOrderService;
import com.scientia.mercatus.service.IPaymentGateway;
import com.scientia.mercatus.service.IPaymentService;
import com.scientia.mercatus.util.CurrencyConversionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {


    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGatewayRegistry gatewayRegistry;
    private final IOrderService orderService;
    private final CurrencyConversionUtil currencyConversion;
    private final PaymentPersistenceService paymentPersistenceService;



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

        long amountMinor = currencyConversion.toINRMinor(order.getTotalAmount());

        IPaymentGateway gateway = gatewayRegistry.get(provider);

        PaymentInitiationResultDto initiationResult = gateway.initiatePayment(
                orderReference,
                amountMinor,
                currency
        );


        Payment payment = new Payment();
        payment.setOrderReference(orderReference);
        payment.setAmountExpected(amountMinor);
        payment.setCurrency(currency);
        payment.setProvider(provider);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProviderOrderId(initiationResult.orderId());
        payment.setProviderPaymentId(null);

        paymentRepository.save(payment);

        return initiationResult;

    }




    @Override
    public void markPaymentSuccess(PaymentProvider provider, String providerOrderId,
                                   String providerPaymentId, long amountReceived) {


        String orderReference = paymentPersistenceService.persistPaymentSuccess(provider, providerOrderId,
                providerPaymentId, amountReceived);
        if (orderReference != null) {
            orderService.finalizePaidOrder(orderReference);
        }
    }


    @Override
    public void markPaymentFailed(PaymentProvider provider, String providerOrderId,
                                  String providerPaymentId) {

        String orderReference = paymentPersistenceService.persistPaymentFailure(provider, providerOrderId,
                providerPaymentId);
        if (orderReference != null) {
            orderService.cancelFailedOrder(orderReference);
        }

    }
}
