package com.scientia.mercatus.inventory.scheduler;

import com.scientia.mercatus.repository.StockReservationRepository;
import com.scientia.mercatus.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationCleanupScheduler {

    private final IInventoryService inventoryService;

    @Transactional
    @Scheduled(fixedDelayString = "${reservation.cleanup-interval:60s}")
    public void cleanupReservations() {
        log.debug("Running scheduler for reservation cleanup");
        inventoryService.expireReservations();

    }
}
