package org.example.ticketreservation.domain.model;

import java.time.Instant;

public record CheckIn(
    String id,
    String reservationCode,
    String ticketCode,
    Long customerId,
    Long tripId,
    String seatNumber,
    boolean checkedIn,
    Instant checkedAt
) {
}
