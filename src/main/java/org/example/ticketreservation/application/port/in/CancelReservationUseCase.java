package org.example.ticketreservation.application.port.in;

import org.example.ticketreservation.domain.model.Reservation;

public interface CancelReservationUseCase {
  Reservation cancel(String reservationCode);
}
