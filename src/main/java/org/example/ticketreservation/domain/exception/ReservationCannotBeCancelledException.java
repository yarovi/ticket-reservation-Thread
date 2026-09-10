package org.example.ticketreservation.domain.exception;

public class ReservationCannotBeCancelledException extends RuntimeException {
  public ReservationCannotBeCancelledException(
      String reservationCode
  ) {
    super(
        "La reserva " + reservationCode
            + " no puede ser cancelada en su estado actual"
    );
  }
}
