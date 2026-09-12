package org.example.ticketreservation.infrastructure.adapter.in.rest.assembler;


import org.example.ticketreservation.domain.model.Seat;
import org.example.ticketreservation.infrastructure.adapter.in.rest.SeatController;
import org.example.ticketreservation.infrastructure.adapter.in.rest.response.SeatResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SeatResponseAssembler {

  public EntityModel<SeatResponse> toModel(
      Seat seat
  ) {

    var response = new SeatResponse(
        seat.id(),
        seat.tripId(),
        seat.seatNumber(),
        seat.status()
    );

    var model = EntityModel.of(response);

    model.add(
        linkTo(
            methodOn(SeatController.class)
                .findSeatsByTrip(seat.tripId())
        ).withRel("trip-seats")
    );

    return model;
  }
}
