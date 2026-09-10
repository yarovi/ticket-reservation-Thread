package org.example.ticketreservation.domain.exception;

public class SeatNotAvailableException extends RuntimeException {
  public SeatNotAvailableException(Long seatId) {
    super("El asiento " + seatId + " ya no se encuentra disponible");
  }
}
