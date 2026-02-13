package com.scientia.mercatus.payment;

import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.service.IPaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;


@RequiredArgsConstructor
@Component
public class PaymentGatewayRegistry {

    private final Map<PaymentProvider, IPaymentGateway> gateways;
    public IPaymentGateway get(PaymentProvider paymentProvider) {
        IPaymentGateway gateway =  gateways.get(paymentProvider);

        if (gateway == null) {
            throw new IllegalArgumentException("No such payment provider: " + paymentProvider);
        }

        return gateway;

    }

}
