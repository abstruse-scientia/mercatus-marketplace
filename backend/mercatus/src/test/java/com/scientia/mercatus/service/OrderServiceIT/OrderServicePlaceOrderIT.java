package com.scientia.mercatus.service.OrderServiceIT;


import com.scientia.mercatus.dto.CartContextDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.mapper.OrderItemMapper;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.ICartService;
import com.scientia.mercatus.service.IOrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
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
class OrderServicePlaceOrderIT {



    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private IOrderService orderService;


    @Autowired
    private UserRepository userRepository;


    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public ICartService service() {
            ICartService mock = Mockito.mock(ICartService.class);

            Mockito.when(mock.resolveCart(Mockito.any(CartContextDto.class)))
                    .thenAnswer(inv -> buildValidCart());
            Mockito.when(mock.lockCartForCheckout(Mockito.anyLong()))
                    .thenAnswer(inv -> buildValidCart());

            return mock;
        }

        @Bean
        public OrderItemMapper orderItemMapper() {
            return new OrderItemMapper();
        }


        private static Cart buildValidCart() {
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

    }


    @BeforeEach
    void setUp() {
        if (!userRepository.existsById(1L)) {
            User user = new User();
            user.setUserId(1L);
            user.setUserName("user1");
            user.setPasswordHash("password1");
            user.setEmail("email1@example.com");
            userRepository.save(user);
        }
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