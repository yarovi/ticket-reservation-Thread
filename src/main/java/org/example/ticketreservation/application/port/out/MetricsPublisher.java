package org.example.ticketreservation.application.port.out;

import java.time.Duration;

public interface MetricsPublisher {
  void increment(String metricsName);
  void record(String metricsName, Duration duration);
}
