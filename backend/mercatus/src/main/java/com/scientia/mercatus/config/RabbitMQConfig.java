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
    public static final String DEAD_LETTER_EXCHANGE = "ecommerce.email.deadLetter.exchange";
    public static final String DEAD_LETTER_QUEUE = "ecommerce.email.deadLetter.queue";
    public static final String DLQ_ROUTING_KEY = "mercatus.email.dlq.routing";


    // Dealy queue
    public static final String DELAY_QUEUE = "mercatus.email.delay.queue";
    public static final String DELAY_EXCHANGE = "mercatus.email.delay.exchange";
    public static final String DELAY_ROUTING_KEY = "mercatus.email.delay.routing";




    // Message format set to standard JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }





    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE, true);
    }



    @Bean
    public Queue emailQueue() { return new  Queue(EMAIL_QUEUE, true); }

    /*This queue's purpose is to just hold the message that were rejected and after ttl
     expires requeue it to original queue.
     It's a neat idea to basically make the main one a dead letter for the delay queue.
     By doing so after the ttl expires the message routed to this delay queue will
     actually be routed to original one.
      */
    @Bean
    public Queue delayQueue(){
        return QueueBuilder.durable(DELAY_QUEUE)
                // Attached to the main queue
                .withArgument("x-dead-letter-exchange", EMAIL_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_ROUTING_KEY)
                .withArgument("x-message-ttl", 30000) // 30 seconds delay
                .build();
    }




    @Bean
    public DirectExchange deadLetterQueueExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public DirectExchange delayQueueExchange() {return new  DirectExchange(DELAY_EXCHANGE);}

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }


    @Bean
    public Binding delayBinding(Queue delayQueue, DirectExchange delayQueueExchange) {
        return BindingBuilder.bind(delayQueue).to(delayQueueExchange).with(DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterQueueExchange){
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterQueueExchange).with(DEAD_LETTER_QUEUE);
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




