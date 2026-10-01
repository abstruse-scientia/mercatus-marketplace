package com.scientia.mercatus.scheduler;

import com.scientia.mercatus.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryReservationCleanupScheduler {

    private final IInventoryService inventoryService;

    @Transactional
    @Scheduled(cron = "${reservation.cleanup-interval}")
    public void cleanupReservations() {
        log.debug("Running scheduler for reservation cleanup");
        inventoryService.expireReservations();

    }
}
