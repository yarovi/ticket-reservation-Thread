package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.model.Ticket;

import java.util.Optional;

public interface TicketRepository {

  Ticket save(Ticket ticket);

  Optional<Ticket> findByTicketCode(String ticketCode);

  Optional<Ticket> findByReservationId(Long reservationId);
}
