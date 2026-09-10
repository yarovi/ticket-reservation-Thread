package org.example;

import org.example.ticketreservation.application.command.ConfirmReservationCommand;
import org.example.ticketreservation.application.port.out.*;
import org.example.ticketreservation.application.service.ConfirmationService;
import org.example.ticketreservation.domain.enums.PaymentMethod;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;
import org.example.ticketreservation.domain.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConfirmationServiceTest {
  private ReservationRepository reservationRepository;
  private PaymentRepository paymentRepository;
  private TicketRepository ticketRepository;
  private SeatRepository seatRepository;
  private CodeGenerator codeGenerator;
  private MetricsPublisher metricsPublisher;

  private ConfirmationService confirmationService;

  @BeforeEach
  void setUp() {

    reservationRepository = mock(ReservationRepository.class);
    paymentRepository = mock(PaymentRepository.class);
    ticketRepository = mock(TicketRepository.class);
    seatRepository = mock(SeatRepository.class);
    codeGenerator = mock(CodeGenerator.class);
    metricsPublisher = mock(MetricsPublisher.class);

    var clock = Clock.fixed(
        Instant.parse("2026-09-09T15:00:00Z"),
        ZoneOffset.UTC
    );

    confirmationService = new ConfirmationService(
        reservationRepository,
        paymentRepository,
        ticketRepository,
        seatRepository,
        codeGenerator,
        metricsPublisher,
        clock
    );
  }

  @Test
  void shouldConfirmReservationAndGenerateTicket() {

    var reservation = new Reservation(
        1L,
        "RSV-123",
        "AB12CD34EF",
        10L,
        20L,
        30L,
        ReservationStatus.PENDING_PAYMENT,
        Instant.parse("2026-09-09T14:45:00Z"),
        Instant.parse("2026-09-09T15:15:00Z")
    );

    when(
        reservationRepository.findByCode("RSV-123")
    ).thenReturn(
        Optional.of(reservation)
    );

    when(
        codeGenerator.ticketCode()
    ).thenReturn("TKT-999XYZ");

    when(
        ticketRepository.save(any())
    ).thenAnswer(
        invocation -> invocation.getArgument(0)
    );

    var command =
        new ConfirmReservationCommand(
            "RSV-123",
            "AB12CD34EF",
            PaymentMethod.MOVIL,
            new BigDecimal("80.00")
        );

    Ticket ticket =
        confirmationService.confirm(command);

    assertEquals(
        "TKT-999XYZ",
        ticket.ticketCode()
    );

    verify(
        paymentRepository
    ).save(any());

    verify(
        reservationRepository
    ).updateStatus(
        1L,
        ReservationStatus.CONFIRMED
    );

    verify(
        seatRepository
    ).markAsSold(30L);

    verify(
        metricsPublisher
    ).increment("reservation.confirmed");
  }

  @Disabled
  void shouldExpireReservationAndReleaseSeat() {

    var reservation = new Reservation(
        1L,
        "RSV-123",
        "AB12CD34EF",
        10L,
        20L,
        30L,
        ReservationStatus.PENDING_PAYMENT,

        Instant.parse(
            "2026-09-09T14:00:00Z"
        ),

        Instant.parse(
            "2026-09-09T14:30:00Z"
        )
    );

    when(
        reservationRepository.findByCode("RSV-123")
    ).thenReturn(
        Optional.of(reservation)
    );

    var command =
        new ConfirmReservationCommand(
            "RSV-123",
            "AB12CD34EF",
            PaymentMethod.MOVIL,
            new BigDecimal("80.00")
        );

    org.junit.jupiter.api.Assertions.assertThrows(
        org.example.ticketreservation.domain.exception
            .ReservationExpiredException.class,
        () -> confirmationService.confirm(command)
    );

    verify(
        reservationRepository
    ).updateStatus(
        1L,
        ReservationStatus.EXPIRED
    );

    verify(
        seatRepository
    ).release(30L);

    verifyNoInteractions(
        paymentRepository
    );

    verifyNoInteractions(
        ticketRepository
    );
  }

}

