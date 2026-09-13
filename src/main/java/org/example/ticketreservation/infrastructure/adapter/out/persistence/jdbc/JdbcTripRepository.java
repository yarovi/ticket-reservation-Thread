package org.example.ticketreservation.infrastructure.adapter.out.persistence.jdbc;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Repository
public class JdbcTripRepository implements TripRepository {

  private static final String FIND_BY_ID_QUERY = "trip.find-by-id";

  private final JdbcTemplate jdbcTemplate;
  private final Properties tripQueries;

  private static final RowMapper<Trip> TRIP_ROW_MAPPER =
      new RowMapper<>() {
        @Override
        public Trip mapRow(ResultSet rs, int rowNum) throws SQLException {

          log.debug("Mapping Trip row. rowNum={}", rowNum);

          return new Trip(
              rs.getLong("id"),
              rs.getString("origin"),
              rs.getString("destination"),
              rs.getLong("transport_id"),
              rs.getTimestamp("departure_at").toLocalDateTime()
          );
        }
      };

  public JdbcTripRepository(
      JdbcTemplate jdbcTemplate,
      @Qualifier("tripQueries") Properties tripQueries
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.tripQueries = tripQueries;
  }

  @Override
  public Optional<Trip> findById(Long id) {

    log.info("Searching trip. tripId={}", id);

    String sql = query(FIND_BY_ID_QUERY);

    log.debug(
        "Executing query. queryKey={}, tripId={}, sql={}",
        FIND_BY_ID_QUERY,
        id,
        sql
    );

    try {

      Optional<Trip> trip = jdbcTemplate.query(
              sql,
              TRIP_ROW_MAPPER,
              id
          )
          .stream()
          .findFirst();

      if (trip.isPresent()) {
        log.info(
            "Trip found. tripId={}, origin={}, destination={}",
            trip.get().id(),
            trip.get().origin(),
            trip.get().destination()
        );
      } else {
        log.warn("Trip not found. tripId={}", id);
      }

      return trip;

    } catch (Exception ex) {

      log.error(
          "Error searching trip. tripId={}, queryKey={}",
          id,
          FIND_BY_ID_QUERY,
          ex
      );

      throw ex;
    }
  }

  private String query(String key) {

    log.debug("Loading SQL query. queryKey={}", key);

    String sql = tripQueries.getProperty(key);

    if (sql == null || sql.isBlank()) {

      log.error("SQL query configuration not found. queryKey={}", key);

      throw new IllegalStateException(
          "No se encontró la query configurada: " + key
      );
    }

    log.debug("SQL query loaded successfully. queryKey={}", key);

    return sql;
  }
}
