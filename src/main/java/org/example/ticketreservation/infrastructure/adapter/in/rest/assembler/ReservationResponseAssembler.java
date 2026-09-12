package org.example.ticketreservation.infrastructure.adapter.in.rest.assembler;

import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;
import org.example.ticketreservation.infrastructure.adapter.in.rest.ReservationController;
import org.example.ticketreservation.infrastructure.adapter.in.rest.response.ReservationResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ReservationResponseAssembler {

  public EntityModel<ReservationResponse> toModel(
      Reservation reservation
  ) {

    var response = new ReservationResponse(
        reservation.id(),
        reservation.reservationCode(),
        reservation.paymentCode(),
        reservation.customerId(),
        reservation.tripId(),
        reservation.seatId(),
        reservation.status(),
        reservation.createdAt(),
        reservation.expiresAt()
    );

    var model = EntityModel.of(response);

    // SELF
    model.add(
        linkTo(
            methodOn(ReservationController.class)
                .findByCode(
                    reservation.reservationCode()
                )
        ).withSelfRel()
    );

    if (reservation.status()
        == ReservationStatus.PENDING_PAYMENT) {

      // CONFIRM
      model.add(
          linkTo(ReservationController.class)
              .slash(reservation.reservationCode())
              .slash("confirmation")
              .withRel("confirm")
      );

      // CANCEL
      model.add(
          linkTo(
              methodOn(ReservationController.class)
                  .cancel(
                      reservation.reservationCode()
                  )
          ).withRel("cancel")
      );
    }

    return model;
  }

}
