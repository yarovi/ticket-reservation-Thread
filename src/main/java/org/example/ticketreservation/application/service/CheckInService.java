package org.example.ticketreservation.application.service;

import org.example.ticketreservation.application.command.CheckInCommand;
import org.example.ticketreservation.application.port.in.CheckInUseCase;
import org.example.ticketreservation.application.port.out.*;
import org.example.ticketreservation.domain.event.PassengerCheckedInEvent;
import org.example.ticketreservation.domain.exception.PassengerAlreadyCheckedInException;
import org.example.ticketreservation.domain.exception.ReservationNotFoundException;
import org.example.ticketreservation.domain.exception.SeatNotFoundException;
import org.example.ticketreservation.domain.exception.TicketNotFoundException;
import org.example.ticketreservation.domain.model.CheckIn;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class CheckInService implements CheckInUseCase {
  private final TicketRepository ticketRepository;
  private final ReservationRepository reservationRepository;
  private final SeatRepository seatRepository;
  private final CheckInRepository checkInRepository;
  private final CheckInEventPublisher eventPublisher;
  private final MetricsPublisher metricsPublisher;
  private final Clock clock;

  public CheckInService(
      ReservationRepository reservationRepository,
      TicketRepository ticketRepository,
      SeatRepository seatRepository,
      CheckInRepository checkInRepository,
      CheckInEventPublisher eventPublisher,
      MetricsPublisher metricsPublisher,
      Clock clock
  ) {
    this.ticketRepository = ticketRepository;
    this.reservationRepository = reservationRepository;
    this.seatRepository = seatRepository;
    this.checkInRepository = checkInRepository;
    this.eventPublisher = eventPublisher;
    this.metricsPublisher = metricsPublisher;
    this.clock = clock;
  }

  @Override
  public CheckIn checkIn(CheckInCommand command) {

    var reservation = reservationRepository
        .findByCode(command.reservationCode())
        .orElseThrow(
            () -> new ReservationNotFoundException(
                command.reservationCode()
            )
        );

    var ticket = ticketRepository
        .findByReservationId(reservation.id())
        .orElseThrow(
            () -> new TicketNotFoundException(
                "reservation=" + command.reservationCode()
            )
        );

    checkAlreadyProcessed(ticket.ticketCode());

    var seat = seatRepository
        .findById(reservation.seatId())
        .orElseThrow(
            () -> new SeatNotFoundException(
                reservation.seatId()
            )
        );

    var checkedAt = Instant.now(clock);

    var checkIn = new CheckIn(
        UUID.randomUUID().toString(),
        reservation.reservationCode(),
        ticket.ticketCode(),
        reservation.customerId(),
        reservation.tripId(),
        seat.seatNumber(),
        true,
        checkedAt
    );

    var savedCheckIn =
        checkInRepository.save(checkIn);

    publishEvent(savedCheckIn);

    metricsPublisher.increment(
        "checkin.completed"
    );

    return savedCheckIn;
  }

  private void checkAlreadyProcessed(String ticketCode) {

    checkInRepository
        .findByReservationCode(ticketCode)
        .ifPresent(existing -> {
          throw new PassengerAlreadyCheckedInException(
              ticketCode
          );
        });
  }

  private void publishEvent(CheckIn checkIn) {

    var event = new PassengerCheckedInEvent(
        checkIn.ticketCode(),
        checkIn.reservationCode(),
        checkIn.customerId(),
        checkIn.tripId(),
        checkIn.seatNumber(),
        checkIn.checkedAt()
    );

    eventPublisher.publish(event);
  }
}
