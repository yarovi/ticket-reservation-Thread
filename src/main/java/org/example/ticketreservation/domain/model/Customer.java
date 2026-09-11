package org.example.ticketreservation.domain.model;

public record Customer(
    Long id,
    String uniqueId,
    String name,
    String email
) {

}
