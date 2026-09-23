package com.scientia.mercatus.messaging;

import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailEventPayloadUtility {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    public EmailEvent getEmailEvent(String orderReference) {
        Order order = orderRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new BusinessException(ErrorEnum.ORDER_NOT_FOUND));
        User user = userRepository.findByUserId(order.getUser().getUserId());
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setOrderReference(orderReference);
        emailEvent.setCustomerName(user.getUserName());
        emailEvent.setEmailAddress(user.getEmail());
        emailEvent.setMessage("Your order has been confirmed!");

        return emailEvent;

    }

}
