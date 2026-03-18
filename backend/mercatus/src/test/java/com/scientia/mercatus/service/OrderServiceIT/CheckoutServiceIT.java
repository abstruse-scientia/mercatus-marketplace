package com.scientia.mercatus.service.OrderServiceIT;


import com.scientia.mercatus.builder.ProductBuilder;

import com.scientia.mercatus.dto.Order.PlaceOrderRequestDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.factory.*;
import com.scientia.mercatus.repository.*;


import com.scientia.mercatus.service.impl.CheckoutService;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CheckoutServiceIT {





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
        Category category;
        Product product;
        InventoryItem inventoryItem;
        UserAddress address;
        CartItem cartItem;
        String orderRef;
        PlaceOrderRequestDto request;
    }

    private CheckoutTestContext baseCheckout() {
        CheckoutTestContext ctx = new CheckoutTestContext();
        ctx.user = userRepository.save(UserFactory.create());
        ctx.cart = cartRepository.save(CartFactory.create(ctx.user));
        ctx.address = addressRepository.save(AddressFactory.withSnapshot(ctx.user.getUserId()));
        ctx.category = categoryRepository.save(CategoryFactory.create());
        ctx.inventoryItem = inventoryItemRepository.save(InventoryItemFactory.create());
        ctx.product = productRepository.save(ProductBuilder.aProduct()
                .withCategory(ctx.category)
                .withSku(ctx.inventoryItem.getSku())
                .build()
        );
        ctx.orderRef = "order-ref" + UUID.randomUUID();
        return ctx;
    }

    private void addCartItem(CheckoutTestContext ctx, int quantity) {
        ctx.cartItem =  cartItemsRepository.save(CartItemFactory.create(ctx.product, ctx.cart,
                    quantity));
    }

    private void addProductToCart(CheckoutTestContext ctx, int quantity) {
        InventoryItem inventoryItem = inventoryItemRepository.save(InventoryItemFactory.create());
        Product product = productRepository.save(ProductBuilder
                .aProduct()
                .withCategory(ctx.category)
                .withSku(inventoryItem.getSku())
                .build()
        );
        cartItemsRepository.save(CartItemFactory.create(product, ctx.cart, quantity));
    }

    private void buildRequest(CheckoutTestContext ctx) {
        ctx.request = new PlaceOrderRequestDto(
                ctx.orderRef,
                ctx.address != null ? ctx.address.getId() : null
        );
    }

    @Test
    @Transactional
    void checkout_shouldPlaceOrderAndReserveInventory() {


        // Arrange
        CheckoutTestContext ctx = baseCheckout();
        addCartItem(ctx, 5);
        buildRequest(ctx);

        // Act
        Order newOrder = checkoutService.checkout(
                ctx.user.getUserId(),
                ctx.request
        );

        entityManager.flush();
        entityManager.clear();

        // Assert

        assertNotNull(newOrder);

        // State persistence
        assertEquals(ctx.user.getUserId(), newOrder.getUser().getUserId());
        assertEquals(ctx.request.orderReference(), newOrder.getOrderReference());
        assertEquals(1, newOrder.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(5000), newOrder.getTotalAmount());

        // Business rule validation
        InventoryItem updatedItem =
                inventoryItemRepository.findById(ctx.inventoryItem.getId()).orElseThrow();

        assertEquals(5, updatedItem.getReservedStock());

        // Reservation created
        StockReservation reservation =
                stockReservationRepository.findByOrderReference(ctx.request.orderReference())
                        .orElseThrow();

        assertEquals(ReservationStatus.RESERVED, reservation.getStatus());

        // Cart cleared
        Cart updatedCart =
                cartRepository.findByCartId(ctx.cart.getCartId()).orElseThrow();

        assertEquals(0, updatedCart.getCartItems().size());

        // Relationship integrity
        Order savedOrder =
                orderRepository.findById(newOrder.getId()).orElseThrow();

        assertEquals(1, savedOrder.getOrderItems().size());

        OrderItem updatedOrderItem =
                savedOrder.getOrderItems().iterator().next();

        assertEquals(savedOrder.getId(), updatedOrderItem.getOrder().getId());

        assertEquals(updatedOrderItem.getProductId(), ctx.product.getProductId());

        assertNotNull(savedOrder.getAddressSnapshot());

        assertEquals(
                ctx.address.getAddressSnapshot().getCity(),
                savedOrder.getAddressSnapshot().getCity()
        );
    }

    @Test
    @Transactional
    void checkout_shouldReturnSameOrder_whenReferenceRepeated() {

        //Arrange
        CheckoutTestContext ctx = baseCheckout();
        addCartItem(ctx, 5);
        buildRequest(ctx);

        //Act
        Order firstOrder = checkoutService.checkout(ctx.user.getUserId(), ctx.request);

        entityManager.flush();
        entityManager.clear();

        Order secondOrder = checkoutService.checkout(ctx.user.getUserId(), ctx.request);

        //Assert
        assertEquals(firstOrder.getId(), secondOrder.getId());
    }


    @Test
    @Transactional
    void checkout_shouldFail_whenInsufficientStock() {

        //Arrange
        CheckoutTestContext ctx = baseCheckout();
        addCartItem(ctx, 13);
        buildRequest(ctx);


        //Assert
        assertThrows(BusinessException.class, ()->
                checkoutService.checkout(ctx.user.getUserId(), ctx.request)
        );

    }

    @Test
    @Transactional
    void checkout_shouldFail_whenCartIsEmpty() {

        //Arrange
        CheckoutTestContext ctx = baseCheckout();
        buildRequest(ctx);


        //Assert
        assertThrows(BusinessException.class, ()->
                checkoutService.checkout(ctx.user.getUserId(), ctx.request));

    }

    @Test
    @Transactional
    void checkout_shouldFail_whenAddressIsEmtpy() {
        CheckoutTestContext ctx = baseCheckout();
        addCartItem(ctx, 5);
        ctx.address = null;
        buildRequest(ctx);
        assertThrows(BusinessException.class, ()->
                checkoutService.checkout(ctx.user.getUserId(), ctx.request));
    }


    @Test
    void checkout_shouldRollBack_whenOneInventoryItemStockIsInsufficient() {

        CheckoutTestContext ctx = baseCheckout();
        addProductToCart(ctx, 5);
        addProductToCart(ctx, 5);
        addProductToCart(ctx, 13);

        buildRequest(ctx);

        assertThrows(BusinessException.class, ()->
                checkoutService.checkout(ctx.user.getUserId(), ctx.request));

        assertEquals(0, orderRepository.count()); // no order formed
        assertEquals(0, stockReservationRepository.count()); // no stock reservation formed
        List<InventoryItem> items = inventoryItemRepository.findAll();
        for (InventoryItem item : items) {
            assertEquals(0, item.getReservedStock());
        }
    }
}

