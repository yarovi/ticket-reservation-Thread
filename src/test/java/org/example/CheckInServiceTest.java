package org.example;

import org.example.ticketreservation.application.command.CheckInCommand;
import org.example.ticketreservation.application.port.out.*;
import org.example.ticketreservation.application.service.CheckInService;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.enums.SeatStatus;
import org.example.ticketreservation.domain.model.Reservation;
import org.example.ticketreservation.domain.model.Seat;
import org.example.ticketreservation.domain.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CheckInServiceTest {
  private TicketRepository ticketRepository;
  private ReservationRepository reservationRepository;
  private SeatRepository seatRepository;
  private CheckInRepository checkInRepository;
  private CheckInEventPublisher eventPublisher;
  private MetricsPublisher metricsPublisher;

  private CheckInService service;

  @BeforeEach
  void setUp() {

    ticketRepository = mock(TicketRepository.class);
    reservationRepository = mock(ReservationRepository.class);
    seatRepository = mock(SeatRepository.class);
    checkInRepository = mock(CheckInRepository.class);
    eventPublisher = mock(CheckInEventPublisher.class);
    metricsPublisher = mock(MetricsPublisher.class);

    var clock = Clock.fixed(
        Instant.parse("2026-09-09T16:00:00Z"),
        ZoneOffset.UTC
    );

    service = new CheckInService(
        ticketRepository,
        reservationRepository,
        seatRepository,
        checkInRepository,
        eventPublisher,
        metricsPublisher,
        clock
    );
  }

  @Test
  void shouldCheckInConfirmedPassenger() {

    var reservation = new Reservation(
        1L,
        "RSV-123",
        "AB12CD34EF",
        10L,
        20L,
        30L,
        ReservationStatus.CONFIRMED,
        Instant.parse("2026-09-09T14:00:00Z"),
        Instant.parse("2026-09-09T14:30:00Z")
    );

    var ticket = new Ticket(
        100L,
        "TKT-999",
        1L,
        Instant.parse("2026-09-09T14:10:00Z")
    );

    var seat = new Seat(
        30L,
        20L,
        "12A",
        SeatStatus.SOLD
    );

    when(
        reservationRepository.findByCode("RSV-123")
    ).thenReturn(
        Optional.of(reservation)
    );

    when(
        ticketRepository.findByReservationId(1L)
    ).thenReturn(
        Optional.of(ticket)
    );

    when(
        checkInRepository.findByReservationCode("TKT-999")
    ).thenReturn(
        Optional.empty()
    );

    when(
        seatRepository.findById(30L)
    ).thenReturn(
        Optional.of(seat)
    );

    when(
        checkInRepository.save(any())
    ).thenAnswer(
        invocation -> invocation.getArgument(0)
    );

    var result = service.checkIn(
        new CheckInCommand("RSV-123")
    );

    assertTrue(result.checkedIn());

    verify(
        checkInRepository
    ).save(any());

    verify(
        eventPublisher
    ).publish(any());

    verify(
        metricsPublisher
    ).increment("checkin.completed");
  }
}
