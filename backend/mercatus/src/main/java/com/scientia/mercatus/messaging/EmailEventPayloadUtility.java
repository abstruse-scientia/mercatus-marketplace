package com.scientia.mercatus.messaging;

import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class EmailEventPayloadUtility {

    private static final String orderTemplate = "order-notification";
    private static final String resetTemplate = "verification-notification";

    // Do not mutate Entity Object; I repeat do not mutate Entity object
    // It should be always use for read operations.


    public EmailEvent forOrderConfirmation(Order order) {
        User user = order.getUser();
        String dedupKey = "dedupKey:" + order.getOrderReference();
        return new EmailEvent.Builder()
                .templateName(orderTemplate)
                .toEmailAddress(user.getEmail())
                .dedupKey(dedupKey)
                .variables(Map.of(
                        "customerName", user.getUserName(),
                        "orderReference", order.getOrderReference(),
                        "message" , "Your order has been confirmed"
                )).build();
    }

    public EmailEvent forForgotPassword(User user, String verificationUrl ) {
        String randomUUID = UUID.randomUUID().toString();
        String dedupKey = "dedupKey:" + randomUUID;
        return new EmailEvent.Builder()
                .templateName(resetTemplate)
                .toEmailAddress(user.getEmail())
                .dedupKey(dedupKey)
                .variables(Map.of(
                        "customerName", user.getUserName(),
                        "verificationUrl", verificationUrl
                )).build();
    }


}
