package org.example.domain.model;

import java.time.Instant;

public record Reservation (
        Long id,

        Long customerId,

        Long ticketId,

        Instant reservationTime
){
}
