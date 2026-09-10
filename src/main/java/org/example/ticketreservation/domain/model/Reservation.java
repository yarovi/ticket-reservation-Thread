package org.example.ticketreservation.domain.model;

import org.example.ticketreservation.domain.enums.ReservationStatus;

import java.time.Clock;
import java.time.Instant;

public record Reservation(
    Long id,
    String reservationCode,
    String paymentCode,
    Long customerId,
    Long tripId,
    Long seatId,
    ReservationStatus status,
    Instant createdAt,
    Instant expiresAt
) {
  public boolean isPendingPayment() {
    return status == ReservationStatus.PENDING_PAYMENT;
  }

  public boolean isExpired(Clock clock) {
    return Instant.now(clock).isAfter(expiresAt);
  }

  public boolean canBeConfirmed(Clock clock) {
    return isPendingPayment() && !isExpired(clock);
  }

  public boolean canBeCancelled() {
    return status == ReservationStatus.PENDING_PAYMENT;
  }
}
