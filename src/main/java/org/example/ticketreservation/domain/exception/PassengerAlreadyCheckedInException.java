package org.example.ticketreservation.domain.exception;

public class PassengerAlreadyCheckedInException extends RuntimeException {
  public PassengerAlreadyCheckedInException(
      String ticketCode
  ) {
    super(
        "El pasajero ya realizó check-in con el ticket: "
            + ticketCode
    );
  }
}
