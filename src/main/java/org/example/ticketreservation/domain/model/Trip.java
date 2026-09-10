package org.example.ticketreservation.domain.model;

import java.time.LocalDateTime;

public record Trip(
    Long id,
    String origin,
    String destination,
    Long transportId,
    LocalDateTime departureAt
) {
}
