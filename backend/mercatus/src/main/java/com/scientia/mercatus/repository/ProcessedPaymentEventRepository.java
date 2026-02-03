package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.ProcessedPaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedPaymentEventRepository extends JpaRepository<ProcessedPaymentEvent,String> {
}
