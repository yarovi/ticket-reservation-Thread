package org.example.domain.model;

public record ReservationRequest(
        Long customerId,

        Integer seat
)

{
}
