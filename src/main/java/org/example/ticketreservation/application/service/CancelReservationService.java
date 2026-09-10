package org.example.ticketreservation.application.service;

import org.example.ticketreservation.application.port.in.CancelReservationUseCase;
import org.example.ticketreservation.application.port.out.MetricsPublisher;
import org.example.ticketreservation.application.port.out.ReservationRepository;
import org.example.ticketreservation.application.port.out.SeatRepository;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.exception.ReservationCannotBeCancelledException;
import org.example.ticketreservation.domain.exception.ReservationNotFoundException;
import org.example.ticketreservation.domain.model.Reservation;

public class CancelReservationService implements CancelReservationUseCase {

  private final ReservationRepository reservationRepository;
  private final SeatRepository seatRepository;
  private final MetricsPublisher metricsPublisher;

  public CancelReservationService(
      ReservationRepository reservationRepository,
      SeatRepository seatRepository,
      MetricsPublisher metricsPublisher
  ) {
    this.reservationRepository = reservationRepository;
    this.seatRepository = seatRepository;
    this.metricsPublisher = metricsPublisher;
  }

  @Override
  public Reservation cancel(String reservationCode) {

    var reservation = reservationRepository
        .findByCode(reservationCode)
        .orElseThrow(
            () -> new ReservationNotFoundException(
                reservationCode
            )
        );

    if (!reservation.canBeCancelled()) {
      throw new ReservationCannotBeCancelledException(
          reservationCode
      );
    }

    reservationRepository.updateStatus(
        reservation.id(),
        ReservationStatus.CANCELLED
    );

    seatRepository.release(
        reservation.seatId()
    );

    metricsPublisher.increment(
        "reservation.cancelled"
    );

    /*
     * Retornamos una representación consistente con
     * el nuevo estado sin obligar al repositorio a
     * realizar una segunda consulta.
     */
    return new Reservation(
        reservation.id(),
        reservation.reservationCode(),
        reservation.paymentCode(),
        reservation.customerId(),
        reservation.tripId(),
        reservation.seatId(),
        ReservationStatus.CANCELLED,
        reservation.createdAt(),
        reservation.expiresAt()
    );
  }
}
