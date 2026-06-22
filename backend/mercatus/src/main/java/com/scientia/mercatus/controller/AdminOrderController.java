package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Order.Admin.AdminOrderSummaryDto;
import com.scientia.mercatus.dto.Order.Admin.UpdateOrderStatusDto;
import com.scientia.mercatus.dto.Order.OrderResponseDto;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Product.Admin.AdminProductResponseDto;
import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.OrderStatus;
import com.scientia.mercatus.mapper.AdminMapper;
import com.scientia.mercatus.service.IOrderService;
import com.stripe.service.climate.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    @Value("${pagination.max-size:10}")
    private int maxPageSize;


    private final IOrderService orderService;
    private final AdminMapper adminMapper;

    @GetMapping("/all")
    public ResponseEntity<Page<AdminOrderSummaryDto>> getOrders(@RequestParam(required = false) OrderStatus status, Pageable pageable) {
        Pageable safePageable = getSafePageable(pageable);
        Page<Order> pagedOrders = orderService.listAllOrders(status, safePageable);
        Page<AdminOrderSummaryDto> pagedResponse = pagedOrders.map(adminMapper::toAdminOrderSummaryDto);
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderSummaryDto> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderByOrderId(id);
        AdminOrderSummaryDto summaryDto =  adminMapper.toAdminOrderSummaryDto(order);
        return ResponseEntity.status(HttpStatus.OK).body(summaryDto);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderStatus>  updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusDto orderStatusDto) {
        Order order = orderService.updateOrderStatus(id, orderStatusDto.status());
        return ResponseEntity.status(HttpStatus.OK).body(order.getStatus());

    }


    private Pageable getSafePageable(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), maxPageSize);
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                sort
        );
    }

}
