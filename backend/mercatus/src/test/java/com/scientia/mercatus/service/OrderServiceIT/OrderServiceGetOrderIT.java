package com.scientia.mercatus.service.OrderServiceIT;


import com.scientia.mercatus.dto.OrderSummaryDto;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.service.IOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false"
})
public class OrderServiceGetOrderIT {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IOrderService  orderService;

    @Test
    void shouldReturnOrdersForUser_withPagination() {

        //Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(
                0,
                5,
                Sort.by("createdAt").descending()

        );

        //Act
        Page<OrderSummaryDto> page = orderService.getOrdersForUser(userId, pageable);

        assertNotNull(page);
        assertTrue(page.getContent().size() <= 5);

        for (OrderSummaryDto Dto : page.getContent()) {
            assertNotNull(Dto.getOrderReference());
        }



    }

}
