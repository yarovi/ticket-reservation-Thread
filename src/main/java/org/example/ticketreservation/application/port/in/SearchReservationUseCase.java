package org.example.ticketreservation.application.port.in;

import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;

import java.util.Optional;
import java.util.List;

public interface SearchReservationUseCase {
  Optional<Reservation> findByCode(String reservationCode);

  List<Reservation> findByStatus(ReservationStatus status);
}
