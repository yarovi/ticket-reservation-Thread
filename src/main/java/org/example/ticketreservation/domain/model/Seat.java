package org.example.ticketreservation.domain.model;

import org.example.ticketreservation.domain.enums.SeatStatus;

public record Seat(
    Long id,
    Long tripId,
    String seatNumber,
    SeatStatus status
) {
  public boolean isAvailable() {
    return status == SeatStatus.AVAILABLE;
  }
}
