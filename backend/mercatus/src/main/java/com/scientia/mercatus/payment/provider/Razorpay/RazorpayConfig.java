package com.scientia.mercatus.payment.provider.Razorpay;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RazorpayConfig {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorPayClient() throws RazorpayException {
        // Validate that credentials are properly configured
        if (keyId == null || keyId.trim().isEmpty() || keyId.equals("default-key-id")) {
            throw new IllegalArgumentException(
                    "Razorpay Key ID not properly configured. " +
                    "Please set 'razorpay.key-id' environment variable with your actual Razorpay key ID"
            );
        }

        if (keySecret == null || keySecret.trim().isEmpty() || keySecret.equals("default-secret-key")) {
            throw new IllegalArgumentException(
                    "Razorpay Key Secret not properly configured. " +
                    "Please set 'razorpay.key.secret' environment variable with your actual Razorpay key secret"
            );
        }

        log.info("Razorpay client initialized with credentials from environment variables");
        return new RazorpayClient(keyId, keySecret);
    }
}
