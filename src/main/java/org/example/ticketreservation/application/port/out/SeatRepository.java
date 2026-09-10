package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.model.Seat;
import java.util.List;
import java.util.Optional;

public interface SeatRepository {

  List<Seat> findByTripId(Long tripId);

  Optional<Seat> findById(Long id);

  boolean reserveIfAvailable(Long seatId);

  void release(Long seatId);
  void markAsSold(Long seatId);
}
