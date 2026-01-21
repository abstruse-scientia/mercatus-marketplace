package com.scientia.mercatus.service;


import com.scientia.mercatus.dto.CartContextDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.mapper.OrderItemMapper;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")

@TestPropertySource(properties = {
        "spring.flyway.enabled=false"
})
class OrderServiceImplITTest {

    private Cart buildValidCart() {
        Product product = new Product();
        product.setProductId(100L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        Cart cart = new Cart();
        cart.setCartId(1L);
        cart.setCartItems(Set.of(cartItem));
        cart.setCartStatus(CartStatus.ACTIVE);

        return cart;
    }


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private IOrderService orderService;   // ← INTERFACE, not impl

    private ICartService cartService = Mockito.mock(ICartService.class);
    private OrderItemMapper orderItemMapper = Mockito.mock(OrderItemMapper.class);

    @BeforeEach
    void setup() {

        User user = new User();
        user.setUserId(1L);
        user.setUserName("user1");
        user.setPasswordHash("password1");
        user.setEmail("email1@example.com");
        userRepository.save(user);

        Cart cart = buildValidCart();

        Mockito.when(cartService.resolveCart(Mockito.any(CartContextDto.class)))
                .thenReturn(cart);

        Mockito.when(cartService.lockCartForCheckout(1L))
                .thenReturn(cart);

        OrderItemMapper realMapper = new OrderItemMapper();

        orderService = new OrderServiceImpl(
                cartService,
                realMapper,
                orderRepository,
                userRepository
        );
    }

    @Test
    void shouldCreateOnlyOneOrder_whenCalledConcurrentlyWithSameOrderReference() throws Exception {
        String orderRef = UUID.randomUUID().toString();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Order> task =
                () -> orderService.placeOrder("session-124", 1L, orderRef);

        Future<Order> future1 = executorService.submit(task);
        Future<Order> future2 = executorService.submit(task);

        Order o1 = future1.get();
        Order o2 = future2.get();

        executorService.shutdown();
        boolean finished =
                executorService.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(finished, "Executor did not terminate in time");

        assertEquals(o1.getId(), o2.getId());

        Optional<Order> existingOrder =
                orderRepository.findByOrderReference(orderRef);

        assertTrue(existingOrder.isPresent());

    }
}