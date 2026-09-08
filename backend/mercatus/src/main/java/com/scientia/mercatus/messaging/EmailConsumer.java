package com.scientia.mercatus.messaging;

import com.rabbitmq.client.Channel;
import com.scientia.mercatus.config.RabbitMQConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer{

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailDeliver(EmailEvent emailEvent,
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag){

        try{
            Context ctx = new Context();
            ctx.setVariable("customer", emailEvent.getCustomerName());
            ctx.setVariable("order", emailEvent.getOrderReference());
            ctx.setVariable("message", emailEvent.getMessage());

            String htmlBody = templateEngine.process("order-notification", ctx);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailEvent.getEmailAddress());
            helper.setSubject("Order Confirmed");
            helper.setText(htmlBody);
            mailSender.send(mimeMessage);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            try {
                channel.basicReject(tag, false);
            }
            catch (Exception ex) {
                log.error("Error rejecting message: {}", ex.getMessage());
            }
        }
    }

}