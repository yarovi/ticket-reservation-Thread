package org.example.domain.result;

public sealed interface ReservationResult
        permits ReservationSuccess,
        ReservationRejected,
        ReservationTimeout {
}
