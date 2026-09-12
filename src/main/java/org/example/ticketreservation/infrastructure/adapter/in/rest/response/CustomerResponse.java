package org.example.ticketreservation.infrastructure.adapter.in.rest.response;

public record CustomerResponse(
    Long id,
    String uniqueId,
    String name,
    String email
) {
}
