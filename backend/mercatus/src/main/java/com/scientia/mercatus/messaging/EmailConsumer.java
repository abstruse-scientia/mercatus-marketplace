package com.scientia.mercatus.messaging;

import com.rabbitmq.client.Channel;
import com.scientia.mercatus.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map;

import static org.springframework.amqp.support.AmqpHeaders.DELIVERY_TAG;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer{

    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private static final int MAX_RETRIES = 4;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailDelivery(@Payload EmailEvent emailEvent,
                                    Channel channel,
                                    @Header(DELIVERY_TAG) long tag,
                                    @Headers Map<String, Object> headers) throws IOException {

        try{
            log.info("Received email event payload");
            emailService.sendEmail(emailEvent);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.info("Executing the catch part due to error while sending email");
            int retryCount = headers.get("retryCount") == null ? 0 : (Integer) headers.get("retryCount");
            if (retryCount < MAX_RETRIES) {
                rabbitTemplate.convertAndSend(RabbitMQConfig.DELAY_EXCHANGE,
                        RabbitMQConfig.DELAY_ROUTING_KEY,
                        emailEvent,
                        message-> {
                                message.getMessageProperties().setHeader("retryCount", retryCount + 1);
                                return message;
                        }
                );
            } else {
                log.debug("Inside the else part of catch block.");
                rabbitTemplate.convertAndSend(RabbitMQConfig.DEAD_LETTER_EXCHANGE,
                        RabbitMQConfig.DLQ_ROUTING_KEY,
                        emailEvent);
            }

            channel.basicAck(tag, false);
        }
    }

}