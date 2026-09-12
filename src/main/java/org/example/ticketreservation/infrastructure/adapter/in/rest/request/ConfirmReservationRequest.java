package org.example.ticketreservation.infrastructure.adapter.in.rest.request;

import org.example.ticketreservation.domain.enums.PaymentMethod;

import java.math.BigDecimal;

public record ConfirmReservationRequest(
    String paymentCode,
    PaymentMethod paymentMethod,
    BigDecimal amount
) {
}
