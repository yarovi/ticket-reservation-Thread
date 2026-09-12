package org.example.ticketreservation.infrastructure.adapter.in.rest.response;

import org.example.ticketreservation.domain.enums.SeatStatus;

public record SeatResponse(
    Long id,
    Long tripId,
    String seatNumber,
    SeatStatus status
) {
}
