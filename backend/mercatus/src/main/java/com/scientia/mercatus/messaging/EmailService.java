package com.scientia.mercatus.messaging;

import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {


    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;


    // sendEmail is initiated via EmailConsumer passing emailEvent payload.
    public void sendEmail(EmailEvent emailEvent) {

        String orderReference = emailEvent.getVariables().get("orderReference");
        try {
            Context ctx = new Context();
//            ctx.setVariable("customerName", emailEvent.getCustomerName());
//            ctx.setVariable("orderReference", emailEvent.getOrderReference());
//            ctx.setVariable("message", emailEvent.getMessage());
            emailEvent.getVariables().forEach(ctx::setVariable);

            String htmlBody = templateEngine.process(emailEvent.getTemplateName(), ctx);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("noreply@mercatus.com");
            helper.setTo(emailEvent.getToEmailAddress());
            helper.setSubject("Order Confirmed");
            helper.setText(htmlBody, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Failed to send email for order reference: {}. Error: {}", orderReference, e.getMessage());
            throw new BusinessException(ErrorEnum.FORBIDDEN_OPERATION,
                    "Failed to send email for order reference: " + orderReference);
        }

    }
}
