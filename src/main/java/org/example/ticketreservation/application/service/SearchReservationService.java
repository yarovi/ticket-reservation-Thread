package org.example.ticketreservation.application.service;

import org.example.ticketreservation.application.port.in.SearchReservationUseCase;
import org.example.ticketreservation.application.port.out.ReservationRepository;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;

import java.util.Optional;
import java.util.List;
public class SearchReservationService implements SearchReservationUseCase {

  private final ReservationRepository reservationRepository;

  public SearchReservationService(
      ReservationRepository reservationRepository
  ) {
    this.reservationRepository = reservationRepository;
  }

  @Override
  public Optional<Reservation> findByCode(String reservationCode) {
    return reservationRepository.findByCode(reservationCode);
  }

  @Override
  public List<Reservation> findByStatus(
      ReservationStatus status
  ) {
    return reservationRepository.findByStatus(status);
  }
}
