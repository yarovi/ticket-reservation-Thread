package org.example.ticketreservation.domain.exception;

public class SeatNotFoundException extends RuntimeException {

  public SeatNotFoundException(Long seatId) {
    super("No existe el asiento con id: " + seatId);
  }
}
