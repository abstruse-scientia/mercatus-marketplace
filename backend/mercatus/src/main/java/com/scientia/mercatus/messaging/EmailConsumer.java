package com.scientia.mercatus.messaging;

import com.rabbitmq.client.Channel;
import com.scientia.mercatus.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.amqp.support.AmqpHeaders.DELIVERY_TAG;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer{

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private static final int MAX_RETRIES = 4;
    private static final int EVICTION_DURATION = 6;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailDelivery(@Payload EmailEvent emailEvent,
                                    Channel channel,
                                    @Header(DELIVERY_TAG) long tag,
                                    @Headers Map<String, Object> headers) throws IOException {

        String dedupKey = emailEvent.getDedupKey(); // get dedup direct from emailPayload
        Boolean if_Absent = redisTemplate.opsForValue().setIfAbsent(dedupKey, "duplication_key",
                EVICTION_DURATION,
                TimeUnit.HOURS);

        if(!Boolean.TRUE.equals(if_Absent)){
            log.info("Deduplication key already exists.{}" ,  dedupKey);
            channel.basicAck(tag, false); // remove the message from the queue.
            return;
        }

        try{
            log.info("Received email event payload");
            emailService.sendEmail(emailEvent);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.info("Executing the catch part due to {} while sending email", e.getMessage());
            redisTemplate.delete(dedupKey); // Evict the key in case of retries.
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