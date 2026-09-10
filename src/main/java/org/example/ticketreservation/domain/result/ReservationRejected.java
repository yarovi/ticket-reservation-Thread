package org.example.ticketreservation.domain.result;

public record ReservationRejected(
        String reason
) implements ReservationResult {
}
