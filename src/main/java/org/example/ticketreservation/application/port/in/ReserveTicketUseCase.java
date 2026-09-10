package org.example.ticketreservation.application.port.in;

import org.example.ticketreservation.application.command.CreateReservationCommand;
import org.example.ticketreservation.domain.model.Reservation;

public interface ReserveTicketUseCase {
  Reservation reserve(CreateReservationCommand command);
}
