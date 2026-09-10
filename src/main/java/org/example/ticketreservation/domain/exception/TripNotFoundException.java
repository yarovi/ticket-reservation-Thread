package org.example.ticketreservation.domain.exception;

public class TripNotFoundException extends RuntimeException {

  public TripNotFoundException(Long tripId) {
    super("No existe el viaje con id: " + tripId);
  }
}
