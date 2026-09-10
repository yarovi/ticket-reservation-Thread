package org.example.ticketreservation.application.service;

import org.example.ticketreservation.application.command.CreateReservationCommand;
import org.example.ticketreservation.application.port.in.ReserveTicketUseCase;
import org.example.ticketreservation.application.port.out.*;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.exception.*;
import org.example.ticketreservation.domain.model.Reservation;
import org.example.ticketreservation.domain.model.Seat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class ReservationService implements ReserveTicketUseCase{
  private final CustomerRepository customerRepository;
  private final TripRepository tripRepository;
  private final SeatRepository seatRepository;
  private final ReservationRepository reservationRepository;
  private final CodeGenerator codeGenerator;
  private final MetricsPublisher metricsPublisher;
  private final Clock clock;

  private final Duration reservationExpiration;

  public ReservationService(
      CustomerRepository customerRepository,
      TripRepository tripRepository,
      SeatRepository seatRepository,
      ReservationRepository reservationRepository,
      CodeGenerator codeGenerator,
      MetricsPublisher metricsPublisher,
      Clock clock,
      Duration reservationExpiration
  ) {
    this.customerRepository = customerRepository;
    this.tripRepository = tripRepository;
    this.seatRepository = seatRepository;
    this.reservationRepository = reservationRepository;
    this.codeGenerator = codeGenerator;
    this.metricsPublisher = metricsPublisher;
    this.clock = clock;
    this.reservationExpiration = reservationExpiration;
  }

  @Override
  public Reservation reserve(CreateReservationCommand command) {

    var startedAt = Instant.now(clock);

    validateCustomer(command.customerId());
    validateTrip(command.tripId());

    var seat = findSeat(command.seatId());

    validateSeatBelongsToTrip(
        seat,
        command.tripId()
    );

    reserveSeat(command.seatId());

    var now = Instant.now(clock);

    var reservation = new Reservation(
        null,
        codeGenerator.reservationCode(),
        codeGenerator.paymentCode(),
        command.customerId(),
        command.tripId(),
        command.seatId(),
        ReservationStatus.PENDING_PAYMENT,
        now,
        now.plus(reservationExpiration)
    );

    var savedReservation =
        reservationRepository.save(reservation);

    metricsPublisher.increment(
        "reservation.created"
    );

    metricsPublisher.record(
        "reservation.creation.duration",
        Duration.between(
            startedAt,
            Instant.now(clock)
        )
    );

    return savedReservation;
  }

  private void validateCustomer(Long customerId) {
    customerRepository.findById(customerId)
        .orElseThrow(
            () -> new CustomerNotFoundException(customerId)
        );
  }

  private void validateTrip(Long tripId) {
    tripRepository.findByTrip(tripId)
        .orElseThrow(
            () -> new TripNotFoundException(tripId)
        );
  }

  private Seat findSeat(Long seatId) {
    return seatRepository.findById(seatId)
        .orElseThrow(
            () -> new SeatNotFoundException(seatId)
        );
  }

  private void validateSeatBelongsToTrip(
      Seat seat,
      Long tripId
  ) {
    if (!seat.tripId().equals(tripId)) {
      throw new InvalidSeatForTripException(
          seat.id(),
          tripId
      );
    }
  }

  private void reserveSeat(Long seatId) {

    boolean reserved =
        seatRepository.reserveIfAvailable(seatId);

    if (!reserved) {
      metricsPublisher.increment(
          "seat.reservation.conflict"
      );

      throw new SeatNotAvailableException(seatId);
    }
  }
}
