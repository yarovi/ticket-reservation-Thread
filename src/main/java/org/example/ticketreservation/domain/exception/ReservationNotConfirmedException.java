package org.example.ticketreservation.domain.exception;

public class ReservationNotConfirmedException extends RuntimeException {
  public ReservationNotConfirmedException(
      String reservationCode
  ) {
    super(
        "La reserva "
            + reservationCode
            + " todavía no se encuentra confirmada"
    );
  }
}
