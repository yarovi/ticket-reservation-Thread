package org.example.ticketreservation.application.command;

public record CreateCustomerCommand(
    String uniqueId,
    String name,
    String email
) {
}
