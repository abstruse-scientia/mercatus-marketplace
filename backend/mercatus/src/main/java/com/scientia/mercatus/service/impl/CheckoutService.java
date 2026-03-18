package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Order.PlaceOrderRequestDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.mapper.OrderMapper;
import com.scientia.mercatus.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CheckoutService {


    private final OrderMapper orderMapper;
    @Value("${inventory.reservation.expiry-minutes:10}")
    private int reservationExpiryMinutes;

    private final IOrderService orderService;
    private final IAddressService addressService;
    private final ICartService cartService;
    private final IInventoryService inventoryService;
    private final IProductService productService;

    @Transactional
    public Order checkout(Long userId, PlaceOrderRequestDto placeOrder) {
        validateUserId(userId);
        Optional<Order> existingOrder = orderService.getExistingOrder(
                userId, placeOrder.orderReference());

        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        Cart currentCart = cartService.lockCartForCheckout(userId);

        if (currentCart.getCartItems().isEmpty()) {
            throw new BusinessException(ErrorEnum.NO_CART_ITEMS_FOUND);
        }

        AddressSnapshot addressSnapshot = addressService
                .getAddressSnapshot(userId, placeOrder.addressId());

        Order newOrder = orderService.makeOrderSkeleton(userId,
                placeOrder.orderReference(),
                addressSnapshot);

        Set<OrderItem> orderItems = reserveAndConvertCartItemToOrderItem(
                currentCart,
                newOrder
        );
        BigDecimal totalPrice = calculateTotal(orderItems);
        newOrder.setOrderItems(orderItems);
        newOrder.setTotalAmount(totalPrice);
        cartService.clearCartAfterCheckout(currentCart);
        return newOrder;
    }

    private Set<OrderItem> reserveAndConvertCartItemToOrderItem(
            Cart currentCart, Order currentOrder) {

        //create expiry for reservation
        Instant expiresAt = Instant.now().plus(reservationExpiryMinutes, ChronoUnit.MINUTES);

        Set<OrderItem> orderItems= new LinkedHashSet<>();
        for(CartItem cartItem: currentCart.getCartItems()) {

            Product product = productService.getActiveProduct(
                    cartItem.getProduct().getProductId()
            );
            //create a valid idempotency key for reservation
            String reservationKey = generateValidReservationKey(
                    currentOrder.getOrderReference(),
                    product.getSku()
            );

            //convert cart item to order item: snapshot the item
            OrderItem item = orderMapper.convertCartItemToOrderItem(cartItem, currentOrder);
            item.setReservationKey(reservationKey);

            inventoryService.reserveStock( // reserve the unit
                    currentOrder.getOrderReference(),
                    reservationKey,
                    product.getSku(),
                    item.getQuantity(),
                    expiresAt
            );
            orderItems.add(item);
        }
        return orderItems;
    }


    private BigDecimal calculateTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getPriceSnapshot().multiply(
                        BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    private String  generateValidReservationKey(String orderReference, String sku) {
        return orderReference + "-" + sku;
    }


    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "User id cannot be null");
        }
    }
}
