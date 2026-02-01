package com.scientia.mercatus.service.OrderServiceIT;


import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.OrderStatus;
import com.scientia.mercatus.entity.OrderPaymentStatus;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.UnauthorizedOperationException;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.IOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false"
})
public class OrderServiceCancelOrderIT {

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IOrderService orderService;

    Long generateUserId() {
        User user = new User();
        user.setUserName("user2");
        user.setPasswordHash("password2");
        user.setEmail(UUID.randomUUID() + "@test.com");
        return userRepository.saveAndFlush(user).getUserId();
    }

    Order createOrder(Long userId) {
        Order order = new Order();
        order.setOrderReference(UUID.randomUUID().toString());
        order.setUser(userRepository.getReferenceByUserId(userId));
        order.setTotalAmount(BigDecimal.valueOf(1240));
        order.setOrderPaymentStatus(OrderPaymentStatus.PENDING);
        order.setStatus(OrderStatus.CREATED);
        return orderRepository.saveAndFlush(order);
    }

    @Test
    void shouldCancelOrderSuccessfully()
    {
        Long userId = generateUserId();
        Order order = createOrder(userId);

        assertTrue(orderRepository.findById(order.getId()).isPresent());
        orderService.cancelOrder(order.getId(), userId);


        Order cancelledOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
        assertEquals(OrderPaymentStatus.CANCELLED, cancelledOrder.getOrderPaymentStatus());

    }

    @Test
    void shouldBeIdempotent_whenCalledTwice() {
        Long userId = generateUserId();
        Order order = createOrder(userId);

        orderService.cancelOrder(order.getId(), userId);
        orderService.cancelOrder(order.getId(), userId);

        Order cancelledOrder = orderRepository.findById(order.getId()).orElseThrow();

        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
    }


    @Test
    void shouldThrowException_whenUserDoesNotOwnOrder() {
        Long userId = generateUserId();
        Order order = createOrder(userId);

        assertThrows(UnauthorizedOperationException.class, () -> orderService.cancelOrder(order.getId(), 3L));
    }

}
