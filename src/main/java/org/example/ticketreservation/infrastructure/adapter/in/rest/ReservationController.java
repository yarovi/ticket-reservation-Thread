package org.example.ticketreservation.infrastructure.adapter.in.rest;

import org.example.ticketreservation.application.command.ConfirmReservationCommand;
import org.example.ticketreservation.application.command.CreateReservationCommand;
import org.example.ticketreservation.application.port.in.CancelReservationUseCase;
import org.example.ticketreservation.application.port.in.ConfirmReservationUseCase;
import org.example.ticketreservation.application.port.in.ReserveTicketUseCase;
import org.example.ticketreservation.application.port.in.SearchReservationUseCase;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;
import org.example.ticketreservation.domain.model.Ticket;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.ConfirmReservationRequest;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.CreateReservationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

  private final ReserveTicketUseCase reserveTicketUseCase;
  private final SearchReservationUseCase searchReservationUseCase;
  private final CancelReservationUseCase cancelReservationUseCase;

  private final ConfirmReservationUseCase confirmReservationUseCase;


  public ReservationController(
      ReserveTicketUseCase reserveTicketUseCase,
      SearchReservationUseCase searchReservationUseCase,
      CancelReservationUseCase cancelReservationUseCase,
      ConfirmReservationUseCase confirmReservationUseCase
  ) {
    this.reserveTicketUseCase = reserveTicketUseCase;
    this.searchReservationUseCase = searchReservationUseCase;
    this.cancelReservationUseCase = cancelReservationUseCase;
    this.confirmReservationUseCase = confirmReservationUseCase;
  }

  @PostMapping
  public ResponseEntity<Reservation> create(
      @RequestBody CreateReservationRequest request
  ) {

    var command = new CreateReservationCommand(
        request.customerId(),
        request.tripId(),
        request.seatId()
    );

    Reservation reservation =
        reserveTicketUseCase.reserve(command);

    URI location = URI.create(
        "/api/v1/reservations/"
            + reservation.reservationCode()
    );

    return ResponseEntity
        .created(location)
        .body(reservation);
  }

  @GetMapping("/{reservationCode}")
  public ResponseEntity<Reservation> findByCode(
      @PathVariable String reservationCode
  ) {

    return searchReservationUseCase
        .findByCode(reservationCode)
        .map(ResponseEntity::ok)
        .orElseGet(
            () -> ResponseEntity.notFound().build()
        );
  }

  @GetMapping
  public ResponseEntity<List<Reservation>> findByStatus(
      @RequestParam ReservationStatus status
  ) {

    return ResponseEntity.ok(
        searchReservationUseCase.findByStatus(status)
    );
  }

  @DeleteMapping("/{reservationCode}")
  public ResponseEntity<Reservation> cancel(
      @PathVariable String reservationCode
  ) {

    Reservation reservation =
        cancelReservationUseCase.cancel(
            reservationCode
        );

    return ResponseEntity.ok(reservation);
  }

  @PostMapping("/{reservationCode}/confirmation")
  public ResponseEntity<Ticket> confirm(
      @PathVariable String reservationCode,
      @RequestBody ConfirmReservationRequest request
  ) {

    var command = new ConfirmReservationCommand(
        reservationCode,
        request.paymentCode(),
        request.paymentMethod(),
        request.amount()
    );

    Ticket ticket =
        confirmReservationUseCase.confirm(command);

    return ResponseEntity.ok(ticket);
  }
}
