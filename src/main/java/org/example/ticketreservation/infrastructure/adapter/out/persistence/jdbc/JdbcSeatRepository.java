package org.example.ticketreservation.infrastructure.adapter.out.persistence.jdbc;

import lombok.AllArgsConstructor;
import org.example.ticketreservation.application.port.out.SeatRepository;
import org.example.ticketreservation.domain.enums.SeatStatus;
import org.example.ticketreservation.domain.model.Seat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.List;
@Repository
public class JdbcSeatRepository implements SeatRepository {

  private final JdbcTemplate jdbcTemplate;
  @Qualifier("seatQueries")
  private final Properties seatQueries;

  public JdbcSeatRepository(
      JdbcTemplate jdbcTemplate,
      Properties seatQueries
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.seatQueries = seatQueries;
  }

  private static final RowMapper<Seat> SEAT_ROW_MAPPER =
      new RowMapper<>() {

        @Override
        public Seat mapRow(ResultSet rs, int rowNum)
            throws SQLException {

          return new Seat(
              rs.getLong("id"),
              rs.getLong("trip_id"),
              rs.getString("seat_number"),
              SeatStatus.valueOf(
                  rs.getString("status")
              )
          );
        }
      };

  @Override
  public List<Seat> findByTripId(Long tripId) {

    String sql = query("seat.find-by-trip-id");

    return jdbcTemplate.query(
        sql,
        SEAT_ROW_MAPPER,
        tripId
    );
  }

  @Override
  public Optional<Seat> findById(Long id) {

    String sql = query("seat.find-by-id");

    return jdbcTemplate.query(
            sql,
            SEAT_ROW_MAPPER,
            id
        )
        .stream()
        .findFirst();
  }

  @Override
  public boolean reserveIfAvailable(Long seatId) {

    String sql = query("seat.reserve-if-available");

    int updatedRows = jdbcTemplate.update(
        sql,
        seatId
    );

    return updatedRows == 1;
  }

  @Override
  public void release(Long seatId) {

    String sql = query("seat.release");

    jdbcTemplate.update(
        sql,
        seatId
    );
  }

  @Override
  public void markAsSold(Long seatId) {

    String sql = query("seat.mark-as-sold");

    jdbcTemplate.update(
        sql,
        seatId
    );
  }

  private String query(String key) {

    String sql = seatQueries.getProperty(key);

    if (sql == null || sql.isBlank()) {
      throw new IllegalStateException(
          "No se encontró la query configurada: " + key
      );
    }

    return sql;
  }
}
