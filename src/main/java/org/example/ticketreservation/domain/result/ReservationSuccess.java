package org.example.ticketreservation.domain.result;

public record ReservationSuccess(
        Long reservationId
) implements ReservationResult {
}
