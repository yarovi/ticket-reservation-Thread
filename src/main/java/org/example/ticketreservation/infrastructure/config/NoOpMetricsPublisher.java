package org.example.ticketreservation.infrastructure.config;

import org.example.ticketreservation.application.port.out.MetricsPublisher;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class NoOpMetricsPublisher implements MetricsPublisher {

  @Override
  public void increment(String metricName) {
  }

  @Override
  public void record(String metricName, Duration duration) {
  }
}
