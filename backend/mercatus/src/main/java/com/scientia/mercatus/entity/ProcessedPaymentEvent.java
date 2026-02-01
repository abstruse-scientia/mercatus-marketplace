package com.scientia.mercatus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "processed_payment_event", schema = "mercatus_db")
public class ProcessedPaymentEvent {
    @Id
    @Size(max = 255)
    @Column(name = "event_id", nullable = false, length = 255)
    private String eventId;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP(6)")
    @Column(name = "processed_at", nullable = false, insertable = false, updatable = false)
    private Instant processedAt;

}