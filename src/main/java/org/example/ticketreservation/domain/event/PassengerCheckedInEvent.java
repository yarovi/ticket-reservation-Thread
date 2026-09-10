package org.example.ticketreservation.domain.event;

import java.time.Instant;

public record PassengerCheckedInEvent(
    String ticketCode,
    String reservationCode,
    Long customerId,
    Long tripId,
    String seatNumber,
    Instant checkedAt
) {
}
