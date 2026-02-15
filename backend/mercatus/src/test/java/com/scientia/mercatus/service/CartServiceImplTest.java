package com.scientia.mercatus.service;


import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.CartItem;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.exception.IllegalQuantity;
import com.scientia.mercatus.repository.CartItemsRepository;
import com.scientia.mercatus.repository.CartRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private IProductService productService;

    @Mock
    private ISessionService ISessionService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartContextDto guestCtx;
    private Cart guestCart;
    private Product product;

    @BeforeEach
    void setUp() {
        guestCtx = new CartContextDto("SESSION-TEST", null);

        guestCart = new Cart();
        guestCart.setSessionId("SESSION-TEST");

        product = new Product();
        product.setProductId(1L);
        product.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void shouldAddNewItem_WhenItemDoesNotExist() {
        when(cartRepository.findBySessionId("SESSION-TEST"))
                .thenReturn(Optional.of(guestCart));
        when(productService.getActiveProduct(1L))
                .thenReturn(product);
        when(cartItemsRepository.findByCartAndProduct(guestCart, product))
                .thenReturn(Optional.empty());

        cartService.addToCart(guestCtx, 1L, 2);

        verify(cartItemsRepository).save(any(CartItem.class));
    }

    @Test
    void shouldIncreaseQuantity_WhenItemExists() {
        CartItem existing = new CartItem();
        existing.setCart(guestCart);
        existing.setProduct(product);
        existing.setQuantity(2);

        when(cartRepository.findBySessionId("SESSION-TEST"))
                .thenReturn(Optional.of(guestCart));
        when(productService.getActiveProduct(1L))
                .thenReturn(product);
        when(cartItemsRepository.findByCartAndProduct(guestCart, product))
                .thenReturn(Optional.of(existing));

        cartService.addToCart(guestCtx, 1L, 5);

        assertEquals(7, existing.getQuantity());
        verify(cartItemsRepository).save(existing);
    }

    @Test
    void shouldThrowIllegalQuantity_WhenQuantityNull() {
        assertThrows(IllegalQuantity.class,
                () -> cartService.addToCart(guestCtx, 1L, null));
    }

    @Test
    void shouldThrowIllegalArgument_WhenProductIdNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(guestCtx, null, 1));
    }

    @Test
    void shouldDeleteItem_WhenItemExists() {
        CartItem item = new CartItem();
        item.setCart(guestCart);
        item.setProduct(product);

        when(cartRepository.findBySessionId("SESSION-TEST"))
                .thenReturn(Optional.of(guestCart));
        when(cartItemsRepository.findByCartAndProduct_ProductId(guestCart, 1L))
                .thenReturn(Optional.of(item));

        cartService.removeFromCart(guestCtx, 1L);

        verify(cartItemsRepository).delete(item);
    }

    @Test
    void shouldDoNothing_WhenItemDoesNotExist_Remove() {
        when(cartRepository.findBySessionId("SESSION-TEST"))
                .thenReturn(Optional.of(guestCart));
        when(cartItemsRepository.findByCartAndProduct_ProductId(guestCart, 1L))
                .thenReturn(Optional.empty());

        cartService.removeFromCart(guestCtx, 1L);

        verify(cartItemsRepository, never()).delete(any());
    }

    @Test
    void shouldThrowIllegalQuantity_WhenUpdateQuantityNull() {
        assertThrows(IllegalQuantity.class,
                () -> cartService.updateQuantity(guestCtx, 1L, null));
    }

    @Test
    void shouldThrowIllegalArgument_WhenUpdateProductIdNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.updateQuantity(guestCtx, null, 1));
    }

    @Test
    void shouldDeleteItem_WhenQuantityZero() {
        CartItem item = new CartItem();
        item.setCart(guestCart);
        item.setProduct(product);
        item.setQuantity(2);

        when(cartRepository.findBySessionId("SESSION-TEST"))
                .thenReturn(Optional.of(guestCart));
        when(cartItemsRepository.findByCartAndProduct_ProductId(guestCart, 1L))
                .thenReturn(Optional.of(item));

        cartService.updateQuantity(guestCtx, 1L, 0);

        verify(cartItemsRepository).delete(item);
    }

    @Test
    void shouldUpdateQuantity_WhenQuantityGreaterThanZero() {
        CartItem item = new CartItem();
        item.setCart(guestCart);
        item.setProduct(product);
        item.setQuantity(1);

        when(cartRepository.findBySessionId("SESSION-TEST"))
                .thenReturn(Optional.of(guestCart));
        when(cartItemsRepository.findByCartAndProduct_ProductId(guestCart, 1L))
                .thenReturn(Optional.of(item));

        cartService.updateQuantity(guestCtx, 1L, 8);

        assertEquals(8, item.getQuantity());
        verify(cartItemsRepository).save(item);
    }
}
