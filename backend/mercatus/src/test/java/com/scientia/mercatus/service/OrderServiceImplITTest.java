package com.scientia.mercatus.service;


import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
public class OrderServiceImplITTest {

    private final IOrderService orderService;
    private final OrderRepository orderRepository;

    @Test
    void shouldCreateOnlyOneOrder_whenCalledConcurrentlyWithSameOrderReference() throws Exception {
        String orderRef = UUID.randomUUID().toString();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Order> task = () -> orderService.placeOrder("session-124", 1L, orderRef);
        Future<Order> future1 = executorService.submit(task);
        Future<Order> future2 = executorService.submit(task);

        Order o1 = future1.get();
        Order o2 = future2.get();

        assertEquals(o1.getId(), o2.getId());

        Optional<Order> existingOrder = orderRepository.findByOrderReference(orderRef);
        assertTrue(existingOrder.isPresent());
        executorService.shutdown();
    }
}
