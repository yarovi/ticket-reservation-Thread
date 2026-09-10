package org.example.ticketreservation.domain.exception;

public class ReservationExpiredException extends RuntimeException {
  public ReservationExpiredException(String reservationCode) {
    super(
        "La reserva "
            + reservationCode
            + " expiró y el asiento fue liberado"
    );
  }
}
