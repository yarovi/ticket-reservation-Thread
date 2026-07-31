package org.example.infraestructure.repository;

import org.example.application.port.out.ReservationRepository;
import org.example.domain.model.Reservation;
import org.example.infraestructure.config.ConnectionFactory;

import java.sql.*;

public class JdbcReservationRepository implements ReservationRepository {

    private static final String INSERT = """
            INSERT INTO reservation (
                customer_id,
                ticket_id,
                reservation_time
            ) VALUES (?, ?, ?)
            """;

    private final ConnectionFactory connectionFactory;

    public JdbcReservationRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    @Override
    public Reservation save(Connection connection, Reservation reservation)  {
        try(PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, reservation.customerId());
            statement.setLong(2, reservation.ticketId());
            statement.setTimestamp(3, java.sql.Timestamp.from(reservation.reservationTime()));
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return new Reservation(id, reservation.customerId(), reservation.ticketId(), reservation.reservationTime());
                } else {
                    throw new SQLException("Creating reservation failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
