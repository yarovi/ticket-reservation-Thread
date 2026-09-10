package org.example.ticketreservation.application.port.in;

import org.example.ticketreservation.application.command.ConfirmReservationCommand;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;
import org.example.ticketreservation.domain.model.Ticket;

import java.util.List;

public interface ConfirmReservationUseCase {
  Ticket confirm(ConfirmReservationCommand command);

}
