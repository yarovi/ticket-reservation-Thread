package org.example.ticketreservation.application.service;

import org.example.ticketreservation.application.port.in.ListSeatUseCase;
import org.example.ticketreservation.application.port.out.SeatRepository;
import org.example.ticketreservation.application.port.out.TripRepository;
import org.example.ticketreservation.domain.exception.TripNotFoundException;
import org.example.ticketreservation.domain.model.Seat;
import java.util.List;
public class ListSeatsService implements ListSeatUseCase {
  private final TripRepository tripRepository;
  private final SeatRepository seatRepository;

  public ListSeatsService(
      TripRepository tripRepository,
      SeatRepository seatRepository
  ) {
    this.tripRepository = tripRepository;
    this.seatRepository = seatRepository;
  }

  @Override
  public List<Seat> findByTrip(Long tripId) {

    tripRepository.findByTrip(tripId)
        .orElseThrow(
            () -> new TripNotFoundException(tripId)
        );

    return seatRepository.findByTripId(tripId);
  }
}