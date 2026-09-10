package org.example.ticketreservation.domain.exception;

public class ReservationNotFoundException extends RuntimeException {
  public ReservationNotFoundException(String reservationCode) {
    super(
        "No existe la reserva con código: "
            + reservationCode
    );
  }
}
