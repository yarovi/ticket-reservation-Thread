package org.example.application.port.in;

import org.example.domain.result.ReservationResult;

public interface MetricsPublisher {
    void publish(ReservationResult result);
}
