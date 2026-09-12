package org.example.ticketreservation.infrastructure.adapter.in.rest.request;

public record CreateCustomerRequest(
    String uniqueId,
    String name,
    String email
) {
}
