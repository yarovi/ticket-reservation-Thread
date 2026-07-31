package org.example.infraestructure.database;

import org.example.application.port.out.TicketRepository;
import org.example.domain.enums.TicketStatus;
import org.example.domain.model.Ticket;
import org.example.infraestructure.config.ConnectionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcTicketRepository implements TicketRepository {

    private static final String FIND_BY_SEAT = """
            SELECT
                id,
                seat,
                status
            FROM ticket
            WHERE seat = ?
            """;

    private static final String UPDATE = """
            UPDATE ticket
               SET status = ?
             WHERE id = ?
            """;

    private final ConnectionFactory connectionFactory;

    public JdbcTicketRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Optional<Ticket> findBySeat(Connection connection, int seat) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_SEAT)) {

            statement.setInt(1, seat);

            try (ResultSet rs = statement.executeQuery()) {

                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(map(rs));

            }

        }
    }


    @Override
    public void update(Connection connection, Ticket ticket) throws SQLException{
        try (PreparedStatement statement =
                     connection.prepareStatement(UPDATE)) {

            statement.setString(
                    1,
                    ticket.status().name());

            statement.setLong(
                    2,
                    ticket.id());

            int rows = statement.executeUpdate();

            if (rows != 1) {
                throw new SQLException(
                        "Unable to update ticket id=" + ticket.id());
            }

        }

    }

    private Ticket map(ResultSet rs)
            throws SQLException {

        return new Ticket(

                rs.getLong("id"),

                rs.getInt("seat"),

                TicketStatus.valueOf(
                        rs.getString("status"))

        );

    }
}
