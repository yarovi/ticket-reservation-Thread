package org.example.ticketreservation.domain.model;

import java.time.Instant;

public record Ticket(

    Long id,
    String ticketCode,
    Long reservationId,
    Instant issuedAt

) {
}
