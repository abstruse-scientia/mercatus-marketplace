package com.scientia.mercatus.payment.provider.Razorpay;


import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RazorpaySignatureVerifier {

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public void verify(String payload, String signature) {
        try {
            Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            throw new SecurityException("Invalid Razorpay webhook signature", e);
        }
    }
}
