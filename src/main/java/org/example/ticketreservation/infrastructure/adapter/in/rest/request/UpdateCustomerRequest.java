package org.example.ticketreservation.infrastructure.adapter.in.rest.request;



public record UpdateCustomerRequest(
    String uniqueId,
    String name,
    String email
) {
}
