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
import org.example.ticketreservation.infrastructure.adapter.in.rest.assembler.ReservationResponseAssembler;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.ConfirmReservationRequest;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.CreateReservationRequest;
import org.example.ticketreservation.infrastructure.adapter.in.rest.response.ReservationResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

  private final ReserveTicketUseCase reserveTicketUseCase;
  private final SearchReservationUseCase searchReservationUseCase;
  private final CancelReservationUseCase cancelReservationUseCase;

  private final ConfirmReservationUseCase confirmReservationUseCase;

  private final ReservationResponseAssembler assembler;

  public ReservationController(
      ReserveTicketUseCase reserveTicketUseCase,
      SearchReservationUseCase searchReservationUseCase,
      CancelReservationUseCase cancelReservationUseCase,
      ConfirmReservationUseCase confirmReservationUseCase,
      ReservationResponseAssembler assembler
  ) {
    this.reserveTicketUseCase = reserveTicketUseCase;
    this.searchReservationUseCase = searchReservationUseCase;
    this.cancelReservationUseCase = cancelReservationUseCase;
    this.confirmReservationUseCase = confirmReservationUseCase;
    this.assembler = assembler;
  }

  /*
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
  */


  //New response with HATEOAS links

  @PostMapping
  public ResponseEntity<EntityModel<ReservationResponse>> create(
      @RequestBody CreateReservationRequest request
  ) {

    var command = new CreateReservationCommand(
        request.customerId(),
        request.tripId(),
        request.seatId()
    );

    Reservation reservation =
        reserveTicketUseCase.reserve(command);

    EntityModel<ReservationResponse> response =
        assembler.toModel(reservation);

    URI location = URI.create(
        "/api/v1/reservations/"
            + reservation.reservationCode()
    );

    return ResponseEntity
        .created(location)
        .body(response);
  }

  @GetMapping("/{reservationCode}")
  public ResponseEntity<EntityModel<ReservationResponse>> findByCode(
      @PathVariable String reservationCode
  ) {

    return searchReservationUseCase
        .findByCode(reservationCode)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(
            () -> ResponseEntity.notFound().build()
        );
  }

  @DeleteMapping("/{reservationCode}")
  public ResponseEntity<EntityModel<ReservationResponse>> cancel(
      @PathVariable String reservationCode
  ) {

    Reservation reservation =
        cancelReservationUseCase.cancel(reservationCode);

    return ResponseEntity.ok(
        assembler.toModel(reservation)
    );
  }

  @GetMapping
  public ResponseEntity<
      CollectionModel<EntityModel<ReservationResponse>>
      > findByStatus(
      @RequestParam ReservationStatus status
  ) {

    var reservations =
        searchReservationUseCase.findByStatus(status);

    var responses = reservations.stream()
        .map(assembler::toModel)
        .toList();

    var collection =
        CollectionModel.of(responses);

    collection.add(
        linkTo(
            methodOn(ReservationController.class)
                .findByStatus(status)
        ).withSelfRel()
    );

    return ResponseEntity.ok(collection);
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
