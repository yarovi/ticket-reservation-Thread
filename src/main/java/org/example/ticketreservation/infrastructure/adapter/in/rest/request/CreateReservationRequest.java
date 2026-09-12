package org.example.ticketreservation.infrastructure.adapter.in.rest.request;

public record CreateReservationRequest(
    Long customerId,
    Long tripId,
    Long seatId
) {
}
