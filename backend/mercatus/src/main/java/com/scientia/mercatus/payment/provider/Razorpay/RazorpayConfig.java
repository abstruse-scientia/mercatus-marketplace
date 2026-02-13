package com.scientia.mercatus.payment.provider.Razorpay;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;


    @Bean
    public RazorpayClient razorPayClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }
}
