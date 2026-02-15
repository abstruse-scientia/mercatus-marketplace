package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.dto.Order.OrderResponseDto;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Payment.CreatePaymentResponseDto;

import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.mapper.OrderMapper;
import com.scientia.mercatus.security.SpringSecurityAuthContext;
import com.scientia.mercatus.service.IOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {


    private final IOrderService orderService;
    private final OrderMapper orderMapper;
    private final SpringSecurityAuthContext authContext;

    @Value("${pagination.max-size:10}")
    private int maxPageSize;


    @PostMapping("/place")
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody CartContextDto cartContextDto,
                                        @RequestParam @NotBlank @Size(max = 255) String orderReference){
        String sessionId = cartContextDto.getSessionId();
        Long userId = cartContextDto.getUserId();
        Order order = orderService.placeOrder(sessionId,userId,orderReference);
        OrderResponseDto responseDto = orderMapper.toResponseDto(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId){
        Long userId = getAuthenticatedUser().getUserId();
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<Page<OrderSummaryDto>> getOrders(Pageable pageable){
        Long userId = getAuthenticatedUser().getUserId();
        int size = Math.min(pageable.getPageSize(),maxPageSize);
        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<OrderSummaryDto> pageDto = orderService.getOrdersForUser(userId, safePageable);
        return ResponseEntity.status(HttpStatus.OK).body(pageDto);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }


    @PostMapping("/{orderId}/pay")
    public ResponseEntity<CreatePaymentResponseDto> makePayment(@PathVariable Long orderId){
        Long userId = authContext.getCurrentUserId();
        PaymentInitiationResultDto initiationResult= orderService.initiatePayment(orderId, userId);
        CreatePaymentResponseDto createPaymentResponseDto = new CreatePaymentResponseDto(
                initiationResult.orderId(),
                initiationResult.amount(),
                initiationResult.currency(),
                initiationResult.paymentProvider()
        );
        return ResponseEntity.ok().body(createPaymentResponseDto);
    }
}
