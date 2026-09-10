package org.example.ticketreservation.domain.model;

public record Transport(
    Long id,
    String code,
    String company,
    String plate
) {
}
