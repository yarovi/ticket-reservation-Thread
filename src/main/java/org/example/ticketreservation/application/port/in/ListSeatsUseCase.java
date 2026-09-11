package org.example.ticketreservation.application.port.in;

import org.example.ticketreservation.domain.model.Seat;
import java.util.List;
public interface ListSeatsUseCase {
  List<Seat> findByTrip(Long tripId);
}
