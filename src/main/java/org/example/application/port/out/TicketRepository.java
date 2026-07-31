package org.example.application.port.out;

import org.example.domain.enums.TicketStatus;
import org.example.domain.model.Ticket;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface TicketRepository {
    Optional<Ticket> findBySeat(Connection connection, int seat) throws SQLException;

    void update(Connection connection,Ticket ticket) throws SQLException;
}
