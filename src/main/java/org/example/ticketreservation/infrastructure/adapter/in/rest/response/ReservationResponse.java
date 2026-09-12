package org.example.ticketreservation.infrastructure.adapter.in.rest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
public record ReservationResponse(
    Long id,
    String reservationCode,
    String paymentCode,
    Long customerId,
    Long tripId,
    Long seatId,
    ReservationStatus status,
    Instant createdAt,
    Instant expiresAt
) {
}
