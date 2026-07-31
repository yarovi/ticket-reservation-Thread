package org.example.infraestructure.metric;

import org.example.application.port.in.MetricsPublisher;

public class ConsoleMetricsPublisher implements MetricsPublisher {
    @Override
    public void publish(org.example.domain.result.ReservationResult result) {
        System.out.println("Reservation Result: " + result);
    }
}
