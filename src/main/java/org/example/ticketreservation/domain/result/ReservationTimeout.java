package org.example.ticketreservation.domain.result;

import java.time.Duration;

public record ReservationTimeout(
        Duration timeout
) implements ReservationResult {
}
