package com.scientia.mercatus.scheduler;

import com.scientia.mercatus.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CancelExpiredOrders {

    private final IOrderService orderService;

    @Scheduled(cron="${order.cleanup-cron}")
    public void cleanupExpiredOrders() {
        log.debug("Running scheduler for all expired orders cleanup");
        orderService.cancelExpiredOrders();
    }
}
