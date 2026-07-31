package org.example.domain.result;

import java.time.Duration;

public record ReservationTimeout(
        Duration timeout
) implements ReservationResult {
}
