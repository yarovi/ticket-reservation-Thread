package org.example.ticketreservation.infrastructure.adapter.out.persistence.jdbc;

import org.example.ticketreservation.application.port.out.TicketRepository;
import org.example.ticketreservation.domain.model.Ticket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Properties;

@Repository
public class JdbcTicketRepository implements TicketRepository {

  private final JdbcTemplate jdbcTemplate;
  private final Properties ticketQueries;

  public JdbcTicketRepository(
      JdbcTemplate jdbcTemplate,
      @Qualifier("ticketQueries") Properties ticketQueries
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.ticketQueries = ticketQueries;
  }

  private static final RowMapper<Ticket> TICKET_ROW_MAPPER =
      new RowMapper<>() {

        @Override
        public Ticket mapRow(ResultSet rs, int rowNum)
            throws SQLException {

          return new Ticket(
              rs.getLong("id"),
              rs.getString("ticket_code"),
              rs.getLong("reservation_id"),
              rs.getTimestamp("issued_at").toInstant()
          );
        }
      };

  @Override
  public Ticket save(Ticket ticket) {

    if (ticket.id() != null) {
      throw new IllegalArgumentException(
          "TicketRepository.save solo crea nuevos tickets"
      );
    }

    String sql = query("ticket.insert");

    Long generatedId = jdbcTemplate.queryForObject(
        sql,
        Long.class,
        ticket.ticketCode(),
        ticket.reservationId(),
        Timestamp.from(ticket.issuedAt())
    );

    return new Ticket(
        generatedId,
        ticket.ticketCode(),
        ticket.reservationId(),
        ticket.issuedAt()
    );
  }

  @Override
  public Optional<Ticket> findByTicketCode(String ticketCode) {
    return Optional.empty();
  }

  @Override
  public Optional<Ticket> findByReservationId(Long reservationId) {

    return jdbcTemplate.query(
            query("ticket.find-by-reservation-id"),
            TICKET_ROW_MAPPER,
            reservationId
        )
        .stream()
        .findFirst();
  }

  private String query(String key) {

    String sql = ticketQueries.getProperty(key);

    if (sql == null || sql.isBlank()) {
      throw new IllegalStateException(
          "No se encontró la query configurada: " + key
      );
    }

    return sql;
  }

}
