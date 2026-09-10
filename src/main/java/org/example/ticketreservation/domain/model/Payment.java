package org.example.ticketreservation.domain.model;

import org.example.ticketreservation.domain.enums.PaymentMethod;
import org.example.ticketreservation.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record Payment(
    Long id,
    Long reservationId,
    String paymentCode,
    PaymentMethod method,
    BigDecimal amount,
    PaymentStatus status,
    Instant createdAt
) {
}
