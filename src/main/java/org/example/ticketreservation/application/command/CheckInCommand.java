package org.example.ticketreservation.application.command;

public record CheckInCommand(
    String reservationCode
) {
}
