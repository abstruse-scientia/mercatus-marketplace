package com.scientia.mercatus.messaging;

import com.scientia.mercatus.config.RabbitMQConfig;



import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {RabbitMQConfig.class, EmailConsumer.class, EmailService.class},
        properties = {
                "logging.level.org.springframework.amqp=DEBUG",
                "logging.level.org.springframework.amqp.rabbit.core.RabbitTemplate=DEBUG",
                "logging.level.org.springframework.amqp.rabbit.listener=DEBUG"}
)
@ImportAutoConfiguration(RabbitAutoConfiguration.class)
@Testcontainers
public class RabbitRetryTest {


    @Container
    static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:4-management");


    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getHost);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
        registry.add("spring.rabbitmq.username", container::getAdminUsername);
        registry.add("spring.rabbitmq.password", container::getAdminPassword);
    }




    @MockitoBean
    private EmailService emailService;


    @MockitoSpyBean
    private RabbitTemplate rabbitTemplate;




    @Test
    public void shouldTriggerCatchPartOfConsumerClass(){


        EmailEvent payload = new EmailEvent.Builder().
                templateName("order-notification.html")
                .toEmailAddress("test@example.com")
                .dedupKey("dedupKey:0X40")
                .variables(Map.of(
                        "customer-name", "customer-name-01",
                        "order-reference", "ord-ref-01",
                        "message", "test-message"
                )).build();
        doThrow(new RuntimeException("Sabotaging try part of consumer class")).
                when(emailService).sendEmail(any(EmailEvent.class));

        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, payload);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitMQConfig.DELAY_EXCHANGE),
                    eq(RabbitMQConfig.DELAY_ROUTING_KEY),
                    any(EmailEvent.class),
                    any(MessagePostProcessor.class)
            );
        });
    }

    @Test
    public void shouldTriggerDLQAfterMaximumRetry(){


        EmailEvent payload = new EmailEvent.Builder().
                templateName("order-notification.html")
                .toEmailAddress("test2@example.com")
                .dedupKey("dedupKey:0X402")
                .variables(Map.of(
                        "customer-name", "customer-name-02",
                        "order-reference", "ord-ref-02",
                        "message", "test-message2"
                )).build();


        doThrow(new RuntimeException("Sabotaging try part of consumer class")).
                when(emailService).sendEmail(any(EmailEvent.class));


        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, payload,
                message -> {
                    message.getMessageProperties().setHeader("retryCount", 4);
                            return message;
                });


        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitMQConfig.DEAD_LETTER_EXCHANGE),
                    eq(RabbitMQConfig.DLQ_ROUTING_KEY),
                    any(EmailEvent.class)
            );
        });
    }



}
