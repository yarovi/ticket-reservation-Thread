package org.example.ticketreservation.infrastructure;

import org.example.ticketreservation.domain.exception.ReservationExpiredException;
import org.example.ticketreservation.domain.model.Ticket;

public record ConfirmationTransactionResult(
    Ticket ticket,
    ReservationExpiredException expirationException
) {

  public static ConfirmationTransactionResult success(Ticket ticket) {
    return new ConfirmationTransactionResult(
        ticket,
        null
    );
  }

  public static ConfirmationTransactionResult expired(
      ReservationExpiredException exception
  ) {
    return new ConfirmationTransactionResult(
        null,
        exception
    );
  }
}
