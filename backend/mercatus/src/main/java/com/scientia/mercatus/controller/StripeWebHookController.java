package com.scientia.mercatus.controller;

import com.scientia.mercatus.service.impl.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/webhooks/stripe")
@RequiredArgsConstructor
public class StripeWebHookController    {



    private final StripeWebhookService stripeWebhookService;

    @PostMapping
    public ResponseEntity<Void> receive(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        stripeWebhookService.process(payload, signature);
        return ResponseEntity.ok().build();
    }

}
