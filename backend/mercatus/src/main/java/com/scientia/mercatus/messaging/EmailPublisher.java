package com.scientia.mercatus.messaging;


import com.scientia.mercatus.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/** Author: abstruse-scientia
 * <p>Of course the correct way to solve this problem is Outbox pattern, but I will not implement it. At least for now.</p>
 * The TransactionalEventListener annotation is used to listen for the events that occur during transaction
 * <p> As for TransactionSynchronizationManager it's used for the sole purpose of avoiding dual write.</p>
 * <p> Dual write happens in this particular event due to checkout phase.
 * Because during transaction commit, the email is sent to the queue and the order status is updated to confirmed.
 * If the email is sent to the queue but the order status update fails, it will cause inconsistency in the system.
 * The email will be sent to the customer but the order status will not be updated to confirmed. It causes dual write problem.
 * To avoid this,  TransactionSynchronizationManager to register a synchronization that will only send the email after the transaction is successfully committed
 * </p>
 */


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final EmailEventPayloadUtility emailEventPayloadUtility;


    // Order object or any kind of jpa bound object should not be passed directly
    // It is to avoid working on cached object or outdated data.
    public void publishEmail(String orderReference) {
        EmailEvent emailEventPayload = emailEventPayloadUtility.getEmailEvent(orderReference);
        // The "if" block executes if the publishEmail method is called within a transaction.
        // It ensures the email is published only if the transaction is committed successfully


        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE,
                            RabbitMQConfig.EMAIL_ROUTING_KEY,
                            emailEventPayload);

                }
            });
        }
        // To make it universal, if the publishEmail method is called outside a transaction, it will still send the email immediately.
        // It makes sure that email publishing is universally applicable.
        else {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    emailEventPayload);
        }
    }

}
