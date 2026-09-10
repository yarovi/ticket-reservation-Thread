package org.example.ticketreservation.application.service;

import org.example.ticketreservation.application.command.ConfirmReservationCommand;
import org.example.ticketreservation.application.port.in.ConfirmReservationUseCase;
import org.example.ticketreservation.application.port.out.*;
import org.example.ticketreservation.domain.enums.PaymentStatus;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.exception.InvalidPaymentCodeException;
import org.example.ticketreservation.domain.exception.ReservationExpiredException;
import org.example.ticketreservation.domain.exception.ReservationNotConfirmedException;
import org.example.ticketreservation.domain.exception.ReservationNotFoundException;
import org.example.ticketreservation.domain.model.Payment;
import org.example.ticketreservation.domain.model.Reservation;
import org.example.ticketreservation.domain.model.Ticket;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class ConfirmationService implements ConfirmReservationUseCase {
  private final ReservationRepository reservationRepository;
  private final PaymentRepository paymentRepository;
  private final TicketRepository ticketRepository;
  private final SeatRepository seatRepository;
  private final CodeGenerator codeGenerator;
  private final MetricsPublisher metricsPublisher;
  private final Clock clock;

  public ConfirmationService(
      ReservationRepository reservationRepository,
      PaymentRepository paymentRepository,
      TicketRepository ticketRepository,
      SeatRepository seatRepository,
      CodeGenerator codeGenerator,
      MetricsPublisher metricsPublisher,
      Clock clock
  ) {
    this.reservationRepository = reservationRepository;
    this.paymentRepository = paymentRepository;
    this.ticketRepository = ticketRepository;
    this.seatRepository = seatRepository;
    this.codeGenerator = codeGenerator;
    this.metricsPublisher = metricsPublisher;
    this.clock = clock;
  }

  @Override
  public Ticket confirm(ConfirmReservationCommand command) {

    var reservation = reservationRepository
        .findByCode(command.reservationCode())
        .orElseThrow(
            () -> new ReservationNotFoundException(
                command.reservationCode()
            )
        );

    validatePaymentCode(
        reservation.paymentCode(),
        command.paymentCode()
    );

    if (reservation.status()
        != ReservationStatus.CONFIRMED) {

      throw new ReservationNotConfirmedException(
          reservation.reservationCode()
      );
    }

    if (reservation.isExpired(clock)) {
      expireReservation(
          reservation.id(),
          reservation.seatId(),
          reservation.reservationCode()
      );
    }

    var now = Instant.now(clock);

    var payment = new Payment(
        null,
        reservation.id(),
        command.paymentCode(),
        command.paymentMethod(),
        command.amount(),
        PaymentStatus.PAID,
        now
    );

    paymentRepository.save(payment);

    reservationRepository.updateStatus(
        reservation.id(),
        ReservationStatus.CONFIRMED
    );

    seatRepository.markAsSold(
        reservation.seatId()
    );

    var ticket = new Ticket(
        null,
        codeGenerator.ticketCode(),
        reservation.id(),
        now
    );

    var savedTicket =
        ticketRepository.save(ticket);

    metricsPublisher.increment(
        "reservation.confirmed"
    );

    metricsPublisher.increment(
        "payment.confirmed"
    );

    return savedTicket;
  }

  @Override
  public List<Reservation> findByStatus(ReservationStatus status) {
    return List.of();
  }

  private void validatePaymentCode(
      String expected,
      String received
  ) {
    if (!expected.equals(received)) {
      throw new InvalidPaymentCodeException();
    }
  }

  private void expireReservation(
      Long reservationId,
      Long seatId,
      String reservationCode
  ) {

    reservationRepository.updateStatus(
        reservationId,
        ReservationStatus.EXPIRED
    );

    seatRepository.release(seatId);

    metricsPublisher.increment(
        "reservation.expired"
    );

    throw new ReservationExpiredException(
        reservationCode
    );
  }

}
