package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.model.CheckIn;
import org.example.ticketreservation.domain.model.Ticket;

import java.util.Optional;

public interface TicketRepository {

  Ticket save(Ticket ticket);

  Optional<CheckIn> findByTicketCode(String ticketCode);
  Optional<Ticket> findByReservationId(Long reservationId);
}
