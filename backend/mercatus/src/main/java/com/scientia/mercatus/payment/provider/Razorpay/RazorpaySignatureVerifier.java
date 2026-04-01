package com.scientia.mercatus.payment.provider.Razorpay;


import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RazorpaySignatureVerifier {

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public RazorpaySignatureVerifier(@Value("${razorpay.webhook.secret}") String webhookSecret) {
        // Validate that webhook secret is properly configured
        if (webhookSecret == null || webhookSecret.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Razorpay Webhook Secret not properly configured. " +
                    "Please set 'razorpay.webhook.secret' environment variable with your actual Razorpay webhook secret"
            );
        }
        this.webhookSecret = webhookSecret;
        log.debug("Razorpay Webhook Secret configured successfully");
    }

    public void verify(String payload, String signature) {
        try {
            Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            throw new SecurityException("Invalid Razorpay webhook signature", e);
        }
    }
}
