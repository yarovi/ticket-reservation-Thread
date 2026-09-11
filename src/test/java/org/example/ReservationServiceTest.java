package org.example;

import org.example.ticketreservation.application.command.ConfirmReservationCommand;
import org.example.ticketreservation.application.command.CreateReservationCommand;
import org.example.ticketreservation.application.port.out.*;
import org.example.ticketreservation.application.service.ConfirmationService;
import org.example.ticketreservation.application.service.ReservationService;
import org.example.ticketreservation.domain.enums.PaymentMethod;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.enums.SeatStatus;
import org.example.ticketreservation.domain.exception.ReservationExpiredException;
import org.example.ticketreservation.domain.model.Customer;
import org.example.ticketreservation.domain.model.Reservation;
import org.example.ticketreservation.domain.model.Seat;
import org.example.ticketreservation.domain.model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {
  private CustomerRepository customerRepository;
  private TripRepository tripRepository;
  private SeatRepository seatRepository;
  private ReservationRepository reservationRepository;
  private CodeGenerator codeGenerator;
  private MetricsPublisher metricsPublisher;

  private ReservationService reservationService;

  private ConfirmationService confirmationService;

  private PaymentRepository paymentRepository;
  private TicketRepository ticketRepository;

  @BeforeEach
  void setUp() {

    customerRepository = mock(CustomerRepository.class);
    tripRepository = mock(TripRepository.class);
    seatRepository = mock(SeatRepository.class);
    reservationRepository = mock(ReservationRepository.class);
    codeGenerator = mock(CodeGenerator.class);
    metricsPublisher = mock(MetricsPublisher.class);
    paymentRepository = mock(PaymentRepository.class);
    ticketRepository = mock(TicketRepository.class);
    confirmationService = mock(ConfirmationService.class);

    Clock clock = Clock.fixed(
        Instant.parse("2026-09-08T20:00:00Z"),
        ZoneOffset.UTC
    );

    reservationService = new ReservationService(
        customerRepository,
        tripRepository,
        seatRepository,
        reservationRepository,
        codeGenerator,
        metricsPublisher,
        clock,
        Duration.ofMinutes(30)
    );
  }

  @Test
  void shouldCreateReservationWhenSeatIsAvailable() {

    var command = new CreateReservationCommand(
        1L,
        10L,
        20L
    );

    var customer = new Customer(
        1L,
        "CLI-001",
        "Juan Perez",
        "juan@email.com"
    );

    var trip = new Trip(
        10L,
        "Arequipa",
        "Cusco",
        100L,
        LocalDateTime.of(
            2026,
            9,
            10,
            20,
            0
        )
    );

    var seat = new Seat(
        20L,
        10L,
        "12A",
        SeatStatus.AVAILABLE
    );

    when(
        customerRepository.findById(1L)
    ).thenReturn(
        Optional.of(customer)
    );

    when(
        tripRepository.findById(10L)
    ).thenReturn(
        Optional.of(trip)
    );

    when(
        seatRepository.findById(20L)
    ).thenReturn(
        Optional.of(seat)
    );

    when(
        seatRepository.reserveIfAvailable(20L)
    ).thenReturn(true);

    when(
        codeGenerator.reservationCode()
    ).thenReturn("RSV-123ABC");

    when(
        codeGenerator.paymentCode()
    ).thenReturn("AB12CD34EF");

    when(
        reservationRepository.save(any())
    ).thenAnswer(invocation ->
        invocation.getArgument(0)
    );

    Reservation result =
        reservationService.reserve(command);

    assertNotNull(result);

    assertEquals(
        "RSV-123ABC",
        result.reservationCode()
    );

    assertEquals(
        "AB12CD34EF",
        result.paymentCode()
    );

    assertEquals(
        ReservationStatus.PENDING_PAYMENT,
        result.status()
    );

    assertEquals(
        Instant.parse("2026-09-08T20:30:00Z"),
        result.expiresAt()
    );

    verify(
        seatRepository
    ).reserveIfAvailable(20L);

    verify(
        metricsPublisher
    ).increment("reservation.created");
  }

  //new case
  @Test
  void shouldRejectReservationWhenSeatIsNotAvailable() {

    var command = new CreateReservationCommand(
        1L,
        10L,
        20L
    );

    var customer = new Customer(
        1L,
        "CLI-001",
        "Juan Perez",
        "juan@email.com"
    );

    var trip = new Trip(
        10L,
        "Arequipa",
        "Cusco",
        100L,
        LocalDateTime.of(
            2026,
            9,
            10,
            20,
            0
        )
    );

    var seat = new Seat(
        20L,
        10L,
        "12A",
        SeatStatus.AVAILABLE
    );

    when(
        customerRepository.findById(1L)
    ).thenReturn(
        Optional.of(customer)
    );

    when(
        tripRepository.findById(10L)
    ).thenReturn(
        Optional.of(trip)
    );

    when(
        seatRepository.findById(20L)
    ).thenReturn(
        Optional.of(seat)
    );

    when(
        seatRepository.reserveIfAvailable(20L)
    ).thenReturn(false);

    assertThrows(
        org.example.ticketreservation.domain.exception
            .SeatNotAvailableException.class,
        () -> reservationService.reserve(command)
    );

    verify(
        metricsPublisher
    ).increment("seat.reservation.conflict");
  }

  //Only test before
  @Test
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

    assertThrows(

        ReservationExpiredException.class,
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
