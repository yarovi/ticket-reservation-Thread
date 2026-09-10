package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;

import java.time.Instant;
import java.util.Optional;
import java.util.List;
public interface ReservationRepository {

  Reservation save(Reservation reservation);

  Optional<Reservation> findByCode(String code);

  Optional<Reservation> findByPaymentCode(String paymentCode);

  List<Reservation> findByStatus(ReservationStatus status);

  List<Reservation> findExpiredReservations(ReservationStatus status, Instant now);

  void updateStatus(Long reservationId, ReservationStatus status);
  Optional<Reservation> findById(Long id);
}
