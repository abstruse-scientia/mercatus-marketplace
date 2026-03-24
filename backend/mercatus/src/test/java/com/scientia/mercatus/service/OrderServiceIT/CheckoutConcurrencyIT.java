package com.scientia.mercatus.service.OrderServiceIT;

import com.scientia.mercatus.builder.ProductBuilder;
import com.scientia.mercatus.dto.Order.PlaceOrderRequestDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.factory.*;
import com.scientia.mercatus.repository.*;
import com.scientia.mercatus.service.impl.CheckoutService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;


@SpringBootTest
@ActiveProfiles("test")
public class CheckoutConcurrencyIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserAddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private StockReservationRepository stockReservationRepository;

    @Autowired
    private EntityManager entityManager;

    private static class CheckoutTestContext {
        User user;
        Cart cart;
        UserAddress address;
        CartItem cartItem;
        String orderRef;
        PlaceOrderRequestDto request;
    }


    //Shared contex must remain same
    private Product sharedContext() {
        Category category = categoryRepository.save(CategoryFactory.create());
        InventoryItem item = inventoryItemRepository.save(InventoryItemFactory.createSingularStock());
        return productRepository.save(ProductBuilder.aProduct()
                        .withCategory(category)
                        .withSku(item.getSku())
                .build());
    }

    //Rest different
    private CheckoutTestContext testContext(Product product) {
        CheckoutTestContext ctx = new CheckoutTestContext();
        ctx.user = userRepository.save(UserFactory.create());
        ctx.cart = cartRepository.save(CartFactory.create(ctx.user));
        ctx.address = addressRepository.save(AddressFactory.withSnapshot(ctx.user.getUserId()));

        ctx.cartItem = cartItemsRepository.save(CartItemFactory.create(product, ctx.cart, 1));
        ctx.orderRef = "order-ref" + UUID.randomUUID();
        ctx.request = new PlaceOrderRequestDto(ctx.orderRef, ctx.address.getId());

        return ctx;
    }


    @Test
    void shouldAllowOnlyOneSuccess_whenMultipleUserPurchaseOneSingleProduct() throws Exception {

        Product product = sharedContext();

        int numberOfUsers = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Boolean>> result = new ArrayList<>();
        for (int i = 0; i < numberOfUsers; i++) {
            result.add(
                    executorService.submit(() -> {
                        latch.await();
                        CheckoutTestContext ctx = testContext(product);
                        try {
                            checkoutService.checkout(ctx.user.getUserId(), ctx.request);
                            return true;
                        }catch (Exception ex){
                            return false;
                        }
                    })
            );
        }

        latch.countDown();
        int successCount = 0;
        int failureCount = 0;
        for (Future<Boolean> future : result) {
            if(future.get()) successCount++;
            else failureCount++;
        }

        InventoryItem item = inventoryItemRepository.findBySku(product.getSku()).orElseThrow();
        Assertions.assertEquals(1, successCount);
        Assertions.assertEquals(4, failureCount);
        Assertions.assertEquals(1, item.getReservedStock());
    }

}
