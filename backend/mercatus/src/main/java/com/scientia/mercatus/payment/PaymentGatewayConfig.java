package com.scientia.mercatus.payment;

import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.service.IPaymentGateway;
import com.scientia.mercatus.service.impl.RazorpayPaymentGateWay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentGatewayConfig {

    @Bean
    public Map<PaymentProvider, IPaymentGateway> paymentGatewayMap(RazorpayPaymentGateWay paymentGateWay) {
        return Map.of(PaymentProvider.RAZORPAY, paymentGateWay);
    }
}
