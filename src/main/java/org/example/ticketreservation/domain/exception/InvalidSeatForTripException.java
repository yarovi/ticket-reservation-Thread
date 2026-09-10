package org.example.ticketreservation.domain.exception;

public class InvalidSeatForTripException extends RuntimeException {

  public InvalidSeatForTripException(Long seatId, Long tripId) {
    super("El asiento con id: " + seatId + " no es válido para el viaje con id: " + tripId);
  }
}
