package org.example.ticketreservation.infrastructure.adapter.out.persistence.jdbc;

import lombok.AllArgsConstructor;
import org.example.ticketreservation.application.port.out.TripRepository;
import org.example.ticketreservation.domain.model.Trip;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

@Repository
public class JdbcTripRepository implements TripRepository {
  private final JdbcTemplate jdbcTemplate;

  @Qualifier("tripQueries")
  private final Properties tripQueries;

  private static final RowMapper<Trip> TRIP_ROW_MAPPER =
      new RowMapper<>() {
        @Override
        public Trip mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new Trip(
              rs.getLong("id"),
              rs.getString("origin"),
              rs.getString("destination"),
              rs.getLong("transport_id"),
              rs.getTimestamp("departure_at").toLocalDateTime()
          );
        }
      };

  public JdbcTripRepository(JdbcTemplate jdbcTemplate, Properties tripQueries) {
    this.jdbcTemplate = jdbcTemplate;
    this.tripQueries = tripQueries;
  }

  @Override
  public Optional<Trip> findById(Long id) {

    String sql = query("trip.find-by-id");

    return jdbcTemplate.query(
            sql,
            TRIP_ROW_MAPPER,
            id
        )
        .stream()
        .findFirst();
  }

  private String query(String key) {

    String sql = tripQueries.getProperty(key);

    if (sql == null || sql.isBlank()) {
      throw new IllegalStateException(
          "No se encontró la query configurada: " + key
      );
    }

    return sql;
  }
}
