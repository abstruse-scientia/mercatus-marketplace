package com.scientia.mercatus.messaging;


import com.redis.testcontainers.RedisContainer;
import com.scientia.mercatus.config.RabbitMQConfig;
import org.awaitility.Awaitility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.thymeleaf.TemplateEngine;


import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = {RabbitMQConfig.class, EmailConsumer.class, EmailService.class},
properties = {
        "logging.level.org.springframework.amqp=DEBUG",
        "logging.level.org.springframework.amqp.rabbit.core.RabbitTemplate=DEBUG",
        "logging.level.org.springframework.amqp.rabbit.listener=DEBUG"}
)
@ImportAutoConfiguration({RabbitAutoConfiguration.class, RedisAutoConfiguration.class})
@Testcontainers
public class ConsumerSideITTest {


    @Container
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:6.2.6"));


    @Container
    static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:4-management");


    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getHost);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
        registry.add("spring.rabbitmq.username", container::getAdminUsername);
        registry.add("spring.rabbitmq.password", container::getAdminPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private JavaMailSender mailSender;

    @MockitoBean
    private TemplateEngine engine;

    private EmailEvent payload;
    @BeforeEach
    void beforeEach() {
        payload = new EmailEvent();
        payload.setOrderReference("order-ref-01");
        payload.setCustomerName("customer-name-01");
        payload.setMessage("Test-message");
        payload.setEmailAddress("test@example.com");
    }


    @Test
    public void shouldTriggerSendEmailMethodInConsumerClass(){


        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                payload);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(emailService, times(1)).sendEmail(any(EmailEvent.class));
        });

    }


    @Test
    public void shouldProcessFirstMessageAndIgnoreDuplicates(){

        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, payload);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, payload);

        String key = "dedup_key:" + payload.getOrderReference();
        System.out.println("The test key is: " + key);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {

            verify(emailService, times(1)).sendEmail(any(EmailEvent.class));

            Boolean lockExists = redisTemplate.hasKey(key);
            assertTrue(lockExists);
        });
    }


}
