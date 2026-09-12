package org.example.ticketreservation.infrastructure.adapter.in.rest;

import org.example.ticketreservation.application.port.in.ListSeatsUseCase;
import org.example.ticketreservation.domain.model.Seat;
import org.example.ticketreservation.infrastructure.adapter.in.rest.assembler.SeatResponseAssembler;
import org.example.ticketreservation.infrastructure.adapter.in.rest.response.SeatResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/trips")
public class SeatController {

  private final ListSeatsUseCase listSeatsUseCase;
  private final SeatResponseAssembler assembler;

  public SeatController(ListSeatsUseCase listSeatsUseCase,
                        SeatResponseAssembler assembler) {
    this.listSeatsUseCase = listSeatsUseCase;
    this.assembler = assembler;
  }

  /*
  @GetMapping("/{tripId}/seats")
  public ResponseEntity<List<Seat>> findSeatsByTrip(
      @PathVariable Long tripId
  ) {

    List<Seat> seats =
        listSeatsUseCase.findByTrip(tripId);

    return ResponseEntity.ok(seats);
  }
  */

  @GetMapping("/{tripId}/seats")
  public ResponseEntity<
      CollectionModel<EntityModel<SeatResponse>>
      > findSeatsByTrip(
      @PathVariable Long tripId
  ) {

    var seats =
        listSeatsUseCase.findByTrip(tripId);

    var responses = seats.stream()
        .map(assembler::toModel)
        .toList();

    var collection =
        CollectionModel.of(responses);

    collection.add(
        linkTo(
            methodOn(SeatController.class)
                .findSeatsByTrip(tripId)
        ).withSelfRel()
    );

    return ResponseEntity.ok(collection);
  }
}
