package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.model.Trip;

import java.util.Optional;

public interface TripRepository {
  Optional<Trip> findByTrip(Long id);
}
