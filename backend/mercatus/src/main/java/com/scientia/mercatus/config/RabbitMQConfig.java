package com.scientia.mercatus.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;


/** What has been configured ?
 * 1. Set up queue for email exchange. Done
 * 2. Set up routing key . Done
 * 3. Set message format to JSON. Done
 * 4. Set up Dead Letter queue for failed messages. Done
 * 5. Deal with side effects: a. enabling publisher confirm
 *  b. dealing with unroutable message. Done
 *
 */


@Configuration
public class RabbitMQConfig {


    // Queue Name
    public static final String EMAIL_QUEUE = "mercatus.email.queue";
    // Exchange Name
    public static final String EMAIL_EXCHANGE = "mercatus.email.exchange";
    // Routing key name
    public static final String EMAIL_ROUTING_KEY = "mercatus.email.send";


    // Dead letter queue constant declaration
    public static final String DEAD_LETTER_X = "ecommerce.email.deadLetter.x";
    public static final String DEAD_LETTER_QUEUE = "ecommerce.email.deadLetter.queue";
    public static final String DLQ_ROUTING_KEY = "mercatus.email.dlq.routing";




    // Message format set to standard JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    /* Config for dead letter queue */

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE, true);
    }

    @Bean
    public DirectExchange deadLetterQueueExchange() {
        return new DirectExchange(DEAD_LETTER_QUEUE);
    }

    // Bind dead letter queue to dead letter exchange
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterQueueExchange())
                .with(DLQ_ROUTING_KEY);
    }



    /* Config for email exchange and binding8 */
    @Bean
    public Queue emailQueue() {
        return  QueueBuilder.durable(EMAIL_QUEUE)
                // Attached dead letter queue to main queue
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_X)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(EMAIL_ROUTING_KEY);
    }




    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());

        // Enabling publisher confirm for reliable publishing
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("CorrelationData: " + correlationData);
            }else {
                System.err.println("Message not confirmed: " + cause);
            }
        });

        // Enable returns for unroutable messages
        template.setReturnsCallback(returned -> {
            System.out.println("Message returned: " + returned.getMessage());
            System.out.println("Reply text:" + returned.getReplyText());
            System.out.println("Reply code:" + returned.getReplyCode());
        });
        return template;
    }
}




