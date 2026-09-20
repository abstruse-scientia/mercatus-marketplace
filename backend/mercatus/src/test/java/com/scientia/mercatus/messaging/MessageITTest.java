package com.scientia.mercatus.messaging;

import com.scientia.mercatus.config.RabbitMQConfig;
import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.entity.UserAddress;
import com.scientia.mercatus.factory.AddressFactory;
import com.scientia.mercatus.factory.OrderFactory;
import com.scientia.mercatus.factory.UserFactory;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.thymeleaf.TemplateEngine;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {EmailConsumer.class, RabbitMQConfig.class, EmailPublisher.class})
@ImportAutoConfiguration(RabbitAutoConfiguration.class)
@Testcontainers
public class MessageITTest {


    @Container
    static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:4-management");


    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getHost);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
        registry.add("spring.rabbitmq.username", container::getAdminUsername);
        registry.add("spring.rabbitmq.password", container::getAdminPassword);
    }


    @Autowired
    private EmailPublisher emailPublisher;

    @Autowired
    private EmailConsumer emailConsumer;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @MockitoBean
    private TemplateEngine templateEngine;

    @Test
    void shouldSendEmailWhenMessageIsPublished() {

        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);
        User mockUser = UserFactory.create();
        UserAddress mockAddress = AddressFactory.withSnapshot(mockUser.getUserId());
        Order mockOrder = OrderFactory.create(mockUser, mockAddress.getAddressSnapshot());

        when(userRepository.findByUserId(mockUser.getUserId())).thenReturn(mockUser);
        when(orderRepository.findByOrderReference(mockOrder.getOrderReference())).thenReturn(Optional.of(mockOrder));
        when(templateEngine.process(anyString(), any())).thenReturn("<html><body>Your order has been confirmed!</body></html>");

        emailPublisher.publishEmail(mockOrder.getOrderReference());

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(
                ()-> verify(mailSender, times(1)).send(any(MimeMessage.class))
        );
    }



}
