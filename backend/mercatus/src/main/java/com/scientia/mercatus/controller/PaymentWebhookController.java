package com.scientia.mercatus.controller;

import com.scientia.mercatus.payment.provider.Razorpay.RazorpayWebhookHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final RazorpayWebhookHandler razorpayWebhookHandler;


    @PostMapping("/razorpay")
    public ResponseEntity<Void> handleRazorpayWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payload) {

        if (signature == null || signature.isBlank()) {
            log.warn("Razorpay webhook received with empty signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            razorpayWebhookHandler.handleWebhook(payload, signature);
            return ResponseEntity.ok().build();
        }catch (SecurityException e) {
            log.warn("Invalid Razorpay webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e) {
            log.warn("Error processing Razorpay webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
