package com.scientia.mercatus.messaging;


import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceUnitTest {


    @Mock
    private JavaMailSender javaMailSender;


    @Mock
    private TemplateEngine templateEngine;


    @InjectMocks
    private EmailService emailService;

    @Test
    void shouldSendEmailWhenMessageIsSent() {

        EmailEvent event = new EmailEvent();
        event.setOrderReference("order-ref-01");
        event.setEmailAddress("test@example.com");
        event.setMessage("test message");

        MimeMessage message = new MimeMessage((Session.getDefaultInstance(System.getProperties())));
        when(javaMailSender.createMimeMessage()).thenReturn(message);
        when(templateEngine.process(anyString(), any())).thenReturn("<html><body>Your order has been confirmed!</body></html>");


        emailService.sendEmail(event);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        });

    }
}
