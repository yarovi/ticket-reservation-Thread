package org.example.ticketreservation.application.command;

public record CreateReservationCommand(
    Long customerId,
    Long tripId,
    Long seatId
) {
}
