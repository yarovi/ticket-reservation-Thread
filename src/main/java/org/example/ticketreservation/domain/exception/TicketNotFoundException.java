package org.example.ticketreservation.domain.exception;

public class TicketNotFoundException extends RuntimeException {
  public TicketNotFoundException(String ticketCode) {
    super(
        "No existe el ticket con código: "
            + ticketCode
    );
  }
}
