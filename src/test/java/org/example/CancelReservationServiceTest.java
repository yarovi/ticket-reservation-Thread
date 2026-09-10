package org.example;

import org.example.ticketreservation.application.port.out.MetricsPublisher;
import org.example.ticketreservation.application.port.out.ReservationRepository;
import org.example.ticketreservation.application.port.out.SeatRepository;
import org.example.ticketreservation.application.service.CancelReservationService;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CancelReservationServiceTest {

  private ReservationRepository reservationRepository;
  private SeatRepository seatRepository;
  private MetricsPublisher metricsPublisher;

  private CancelReservationService service;

  @BeforeEach
  void setUp() {

    reservationRepository = mock(ReservationRepository.class);

    seatRepository = mock(SeatRepository.class);

    metricsPublisher = mock(MetricsPublisher.class);

    service = new CancelReservationService(reservationRepository, seatRepository, metricsPublisher);
  }

  @Test
  void shouldCancelPendingReservationAndReleaseSeat() {

    var reservation = new Reservation(
        1L,
        "RSV-123",
        "AB12CD34EF",
        10L,
        20L,
        30L,
        ReservationStatus.PENDING_PAYMENT,
        Instant.parse("2026-09-09T14:00:00Z"),
        Instant.parse("2026-09-09T14:30:00Z"));

    when(reservationRepository.findByCode("RSV-123")).thenReturn(Optional.of(reservation));

    var result = service.cancel("RSV-123");

    assertEquals(ReservationStatus.CANCELLED, result.status());

    verify(reservationRepository).updateStatus(1L, ReservationStatus.CANCELLED);

    verify(seatRepository).release(30L);

    verify(metricsPublisher).increment("reservation.cancelled");
  }
}
