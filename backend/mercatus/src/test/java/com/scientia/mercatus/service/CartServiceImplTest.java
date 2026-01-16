package com.scientia.mercatus.service;


import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.CartItems;
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


        BigDecimal quantity = BigDecimal.TWO;

        product.setPrice(BigDecimal.valueOf(100));

        when(productRepository.findByProductId(1L))
                .thenReturn(Optional.of(product));

        when(cartItemsRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.empty());


        //Act
        cartService.addToCart(cart, 1L, quantity);

        verify(cartItemsRepository).save(any(CartItems.class));

    }

    @Test
    void shouldThrowIllegalQuantityException_WhenQuantityIsNull() {

            assertThrows(IllegalQuantity.class, () -> {
                cartService.addToCart(cart, 1L, null);
            });
    }

    @Test
    void shouldIncreaseQuantity_WhenItemDoesExist() {
        BigDecimal quantityAdd = BigDecimal.valueOf(5);

        product.setPrice(BigDecimal.valueOf(100));

        CartItems existingCartItems = new  CartItems();
        existingCartItems.setCart(cart);
        existingCartItems.setProduct(product);
        existingCartItems.setQuantity(BigDecimal.TWO);

        when(productRepository.findByProductId(1L))
                .thenReturn(Optional.of(product));

        when(cartItemsRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.of(existingCartItems));


        cartService.addToCart(cart, 1L, quantityAdd);

        assertEquals(BigDecimal.valueOf(7), existingCartItems.getQuantity());
        verify(cartItemsRepository).save(any(CartItems.class));
        verify(cartItemsRepository, times(1)).save(existingCartItems);


    }


    @Test
    void shouldThrowIllegalArgumentException_whenProductIdIsNull() {
        product.setProductId(null);
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(cart, null, BigDecimal.ONE);
        });
    }

//Tests related to remove from cart method

    @Test
    void shouldDeleteItemFromCart_WhenItemExists() {

        CartItems existingCartItems = new  CartItems();
        existingCartItems.setCart(cart);
        existingCartItems.setProduct(product);


        when(productRepository.findByProductId(1L)).
                thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartAndProduct(cart, product)).
                thenReturn(Optional.of(existingCartItems));

        //Arrange
        cartService.removeFromCart(cart, 1L);

        //Assert
        verify(cartItemsRepository, times(1)).delete(existingCartItems);

    }

    @Test
    void shouldDoNothing_WhenItemDoesNotExist() {
        when(productRepository.findByProductId(1L)).
                thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartAndProduct(cart, product)).
                thenReturn(Optional.empty());


        cartService.removeFromCart(cart, 1L);

        verify(cartItemsRepository, never()).delete(any(CartItems.class));
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
            cartService.updateQuantity(cart, null, BigDecimal.ONE)
        );
    }

    @Test
    void shouldThrowRunTimeException_whenProductNotFound() {

        when(productRepository.findByProductId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                cartService.updateQuantity(cart, 1L, BigDecimal.ONE)
        );
    }

    @Test
    void shouldDoNothing_whenCartItemDoesNotExist() {
        CartItems existingCartItems = new  CartItems();
        existingCartItems.setCart(cart);
        existingCartItems.setProduct(product);

        when(productRepository.findByProductId(1L)).thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        cartService.updateQuantity(cart, 1L, BigDecimal.ONE);
        verify(cartItemsRepository, never()).save(any(CartItems.class));
    }


    @Test
    void  shouldDeleteItemFromCart_WhenQuantityIsZero() {
        CartItems existingCartItems = new  CartItems();
        existingCartItems.setCart(cart);
        existingCartItems.setProduct(product);
        existingCartItems.setQuantity(BigDecimal.TWO);

        when(productRepository.
                findByProductId(1L)).thenReturn(Optional.of(product));
        when(cartItemsRepository.
                findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingCartItems));

    cartService.updateQuantity(cart, 1L, BigDecimal.ZERO);

        verify(cartItemsRepository).delete(existingCartItems);

    }

    @Test
    void shouldSetQuantity_WhenQuantityGreaterThanZero() {
        CartItems existingCartItems = new  CartItems();
        existingCartItems.setCart(cart);
        existingCartItems.setProduct(product);
        existingCartItems.setQuantity(BigDecimal.ONE);

        when(productRepository.
                findByProductId(1L)).thenReturn(Optional.of(product));
        when(cartItemsRepository.
                findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingCartItems));

        cartService.updateQuantity(cart, 1L, BigDecimal.valueOf(8));

        assertEquals(BigDecimal.valueOf(8), existingCartItems.getQuantity());
    }

    //Tests Related to getCartDetails method

    @Test
    void shouldReturnCartResponseDto_WhenCartIsPresent() {
        List<CartItems> items = new ArrayList<>();
        CartItems item = new CartItems();
        item.setCart(cart);
        item.setQuantity(BigDecimal.ONE);
        item.setPriceSnapshot(BigDecimal.valueOf(4000));
    }

}

