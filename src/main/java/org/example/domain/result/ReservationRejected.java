package org.example.domain.result;

public record ReservationRejected(
        String reason
) implements ReservationResult {
}
