package com.scientia.mercatus.messaging;

import com.rabbitmq.client.Channel;
import com.scientia.mercatus.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.Delivery;

import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer{

    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private static final int MAX_RETRIES = 4;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailDelivery(EmailEvent emailEvent,
                                    Channel channel,
                                    Delivery delivery) throws IOException {

        long tag = delivery.getEnvelope().getDeliveryTag();
        try{
            emailService.sendEmail(emailEvent);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            Map<String, Object> headers = delivery.getProperties().getHeaders();
            int retryCount = headers.get("retry-count") == null ? 0 : (int) headers.get("retry-count");
            if (retryCount < MAX_RETRIES) {
                headers.put("retry-count", retryCount + 1);
                rabbitTemplate.convertAndSend(RabbitMQConfig.DELAY_QUEUE,
                        RabbitMQConfig.DELAY_ROUTING_KEY,
                        emailEvent
                );
            } else {
                rabbitTemplate.convertAndSend(RabbitMQConfig.DEAD_LETTER_QUEUE,
                        RabbitMQConfig.DLQ_ROUTING_KEY);
            }

            channel.basicAck(tag, false);
        }
    }

}