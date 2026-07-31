package org.example.domain.result;

public record ReservationSuccess(
        Long reservationId
) implements ReservationResult {
}
