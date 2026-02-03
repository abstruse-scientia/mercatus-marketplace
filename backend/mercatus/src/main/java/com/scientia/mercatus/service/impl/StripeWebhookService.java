package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.ProcessedPaymentEvent;
import com.scientia.mercatus.repository.ProcessedPaymentEventRepository;
import com.scientia.mercatus.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.exception.SignatureVerificationException;


@Service
@RequiredArgsConstructor
@Transactional
public class StripeWebhookService {

    private final IPaymentService paymentService;
    private final ProcessedPaymentEventRepository eventRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;


    public void process(String payload, String signatureHeader) {


        Event event = verifySignature(payload, signatureHeader);


        if (eventRepository.existsById(event.getId())) {
            return;
        }


        handleEvent(event);


        ProcessedPaymentEvent processedPaymentEvent = new ProcessedPaymentEvent();
        processedPaymentEvent.setEventId(event.getId());
        eventRepository.save(processedPaymentEvent);
    }


    private Event verifySignature(String payload, String signatureHeader) {
        try {
            return Webhook.constructEvent(
                    payload,
                    signatureHeader,
                    webhookSecret
            );
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Invalid Stripe webhook signature", e);
        }
    }


    private void handleEvent(Event event) {

        switch (event.getType()) {

            case "payment_intent.succeeded" -> {
                String providerPaymentId = extractPaymentIntentId(event);
                paymentService.markPaymentSuccess(providerPaymentId);
            }

            case "payment_intent.payment_failed" -> {
                String providerPaymentId = extractPaymentIntentId(event);
                paymentService.markPaymentFailed(providerPaymentId);
            }

            default -> {

            }
        }
    }

    private String extractPaymentIntentId(Event event) {
        PaymentIntent intent = (PaymentIntent) event
                .getDataObjectDeserializer()
                .getObject()
                .orElseThrow();
        return intent.getId();
    }
}
