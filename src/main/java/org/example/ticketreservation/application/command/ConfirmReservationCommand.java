package org.example.ticketreservation.application.command;

import org.example.ticketreservation.domain.enums.PaymentMethod;

import java.math.BigDecimal;

public record ConfirmReservationCommand (
    String reservationCode,
    String paymentCode,
    PaymentMethod paymentMethod,
    BigDecimal amount
){
}
