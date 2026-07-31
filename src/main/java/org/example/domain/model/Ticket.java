package org.example.domain.model;

import org.example.domain.enums.TicketStatus;

public record Ticket(

        Long id,

        Integer seat,

        TicketStatus status

) {
}
