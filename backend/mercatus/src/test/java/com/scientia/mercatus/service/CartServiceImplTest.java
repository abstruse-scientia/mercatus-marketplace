package com.scientia.mercatus.service;


import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.CartItem;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.exception.IllegalQuantity;
import com.scientia.mercatus.repository.CartItemsRepository;
import com.scientia.mercatus.repository.CartRepository;
import com.scientia.mercatus.repository.ProductRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;


    @Mock
    private CartItemsRepository  cartItemsRepository;


    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private Product product;

    @BeforeEach
    public void setUp() {
        cart = new Cart();
        product = new Product();
        Long productId = 1L;
        product.setProductId(productId);
        product.setPrice(BigDecimal.valueOf(100));

    }



//    Tests related to add to cart function

    @Test
    void shouldAddNewItemToCart_WhenItemDoesNotExist() {


        product.setPrice(BigDecimal.valueOf(100));

        when(productRepository.findByProductId(1L))
                .thenReturn(Optional.of(product));

        when(cartItemsRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.empty());


        //Act
        cartService.addToCart(cart, 1L, 2);

        verify(cartItemsRepository).save(any(CartItem.class));

    }

    @Test
    void shouldThrowIllegalQuantityException_WhenQuantityIsNull() {

            assertThrows(IllegalQuantity.class, () -> {
                cartService.addToCart(cart, 1L, null);
            });
    }

    @Test
    void shouldIncreaseQuantity_WhenItemDoesExist() {

        product.setPrice(BigDecimal.valueOf(100));

        CartItem existingCartItem = new CartItem();
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(2);

        when(productRepository.findByProductId(1L))
                .thenReturn(Optional.of(product));

        when(cartItemsRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.of(existingCartItem));


        cartService.addToCart(cart, 1L, 5);

        assertEquals(7, existingCartItem.getQuantity());
        verify(cartItemsRepository).save(any(CartItem.class));
        verify(cartItemsRepository, times(1)).save(existingCartItem);


    }


    @Test
    void shouldThrowIllegalArgumentException_whenProductIdIsNull() {
        product.setProductId(null);
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(cart, null, 1);
        });
    }

//Tests related to remove from cart method

    @Test
    void shouldDeleteItemFromCart_WhenItemExists() {

        CartItem existingCartItem = new CartItem();
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);


        when(productRepository.findByProductId(1L)).
                thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartAndProduct(cart, product)).
                thenReturn(Optional.of(existingCartItem));

        //Arrange
        cartService.removeFromCart(cart, 1L);

        //Assert
        verify(cartItemsRepository, times(1)).delete(existingCartItem);

    }

    @Test
    void shouldDoNothing_WhenItemDoesNotExist() {
        when(productRepository.findByProductId(1L)).
                thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartAndProduct(cart, product)).
                thenReturn(Optional.empty());


        cartService.removeFromCart(cart, 1L);

        verify(cartItemsRepository, never()).delete(any(CartItem.class));
    }

    //Tests related to updateQuantity method
    @Test
    void shouldThrowIllegalQuantityException_whenQuantityIsNull() {
        assertThrows(IllegalQuantity.class, () ->
                cartService.updateQuantity(cart, 1L, null));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenProductIdIsNullUpdateQuantityMethod() {

        assertThrows(IllegalArgumentException.class, () ->
            cartService.updateQuantity(cart, null, 1)
        );
    }

    @Test
    void shouldThrowRunTimeException_whenProductNotFound() {

        when(productRepository.findByProductId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                cartService.updateQuantity(cart, 1L, 1)
        );
    }

    @Test
    void shouldDoNothing_whenCartItemDoesNotExist() {
        CartItem existingCartItem = new CartItem();
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);

        when(productRepository.findByProductId(1L)).thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        cartService.updateQuantity(cart, 1L, 1);
        verify(cartItemsRepository, never()).save(any(CartItem.class));
    }


    @Test
    void  shouldDeleteItemFromCart_WhenQuantityIsZero() {
        CartItem existingCartItem = new CartItem();
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(2);

        when(productRepository.
                findByProductId(1L)).thenReturn(Optional.of(product));
        when(cartItemsRepository.
                findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingCartItem));

    cartService.updateQuantity(cart, 1L, 0);

        verify(cartItemsRepository).delete(existingCartItem);

    }

    @Test
    void shouldSetQuantity_WhenQuantityGreaterThanZero() {
        CartItem existingCartItem = new CartItem();
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(1);

        when(productRepository.
                findByProductId(1L)).thenReturn(Optional.of(product));
        when(cartItemsRepository.
                findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingCartItem));

        cartService.updateQuantity(cart, 1L, 8);

        assertEquals(8, existingCartItem.getQuantity());
    }

    //Tests Related to getCartDetails method

    @Test
    void shouldReturnCartResponseDto_WhenCartIsPresent() {
        List<CartItem> items = new ArrayList<>();
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setQuantity(1);

    }

}

