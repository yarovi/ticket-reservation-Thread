package org.example.ticketreservation.domain.result;

public sealed interface ReservationResult
        permits ReservationSuccess,
        ReservationRejected,
        ReservationTimeout {
}
