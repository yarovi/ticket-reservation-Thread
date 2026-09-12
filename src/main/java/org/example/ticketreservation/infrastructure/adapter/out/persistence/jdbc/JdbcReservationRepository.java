package org.example.ticketreservation.infrastructure.adapter.out.persistence.jdbc;

import org.example.ticketreservation.application.port.out.ReservationRepository;
import org.example.ticketreservation.domain.enums.ReservationStatus;
import org.example.ticketreservation.domain.model.Reservation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import java.util.List;
@Repository
public class JdbcReservationRepository implements ReservationRepository {

  private final JdbcTemplate jdbcTemplate;
  @Qualifier("reservationQueries")
  private final Properties reservationQueries;

  public JdbcReservationRepository(
      JdbcTemplate jdbcTemplate,
       Properties reservationQueries
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.reservationQueries = reservationQueries;
  }

  private static final RowMapper<Reservation> RESERVATION_ROW_MAPPER =
      new RowMapper<>() {

        @Override
        public Reservation mapRow(ResultSet rs, int rowNum)
            throws SQLException {

          return new Reservation(
              rs.getLong("id"),
              rs.getString("reservation_code"),
              rs.getString("payment_code"),
              rs.getLong("customer_id"),
              rs.getLong("trip_id"),
              rs.getLong("seat_id"),
              ReservationStatus.valueOf(
                  rs.getString("status")
              ),
              rs.getTimestamp("created_at").toInstant(),
              rs.getTimestamp("expires_at").toInstant()
          );
        }
      };

  @Override
  public Reservation save(Reservation reservation) {

    if (reservation.id() != null) {
      throw new IllegalArgumentException(
          "ReservationRepository.save solo crea nuevas reservas"
      );
    }

    String sql = query("reservation.insert");

    Long generatedId = jdbcTemplate.queryForObject(
        sql,
        Long.class,
        reservation.reservationCode(),
        reservation.paymentCode(),
        reservation.customerId(),
        reservation.tripId(),
        reservation.seatId(),
        reservation.status().name(),
        Timestamp.from(reservation.createdAt()),
        Timestamp.from(reservation.expiresAt())
    );

    return new Reservation(
        generatedId,
        reservation.reservationCode(),
        reservation.paymentCode(),
        reservation.customerId(),
        reservation.tripId(),
        reservation.seatId(),
        reservation.status(),
        reservation.createdAt(),
        reservation.expiresAt()
    );
  }

  @Override
  public Optional<Reservation> findById(Long id) {

    return jdbcTemplate.query(
            query("reservation.find-by-id"),
            RESERVATION_ROW_MAPPER,
            id
        )
        .stream()
        .findFirst();
  }

  @Override
  public Optional<Reservation> findByCode(String code) {

    return jdbcTemplate.query(
            query("reservation.find-by-code"),
            RESERVATION_ROW_MAPPER,
            code
        )
        .stream()
        .findFirst();
  }

  @Override
  public Optional<Reservation> findByPaymentCode(String paymentCode) {

    return jdbcTemplate.query(
            query("reservation.find-by-payment-code"),
            RESERVATION_ROW_MAPPER,
            paymentCode
        )
        .stream()
        .findFirst();
  }

  @Override
  public List<Reservation> findByStatus(ReservationStatus status) {

    return jdbcTemplate.query(
        query("reservation.find-by-status"),
        RESERVATION_ROW_MAPPER,
        status.name()
    );
  }

  @Override
  public List<Reservation> findExpiredReservations(
      ReservationStatus status,
      Instant now
  ) {

    return jdbcTemplate.query(
        query("reservation.find-expired"),
        RESERVATION_ROW_MAPPER,
        status.name(),
        Timestamp.from(now)
    );
  }

  @Override
  public void updateStatus(
      Long reservationId,
      ReservationStatus status
  ) {

    int updatedRows = jdbcTemplate.update(
        query("reservation.update-status"),
        status.name(),
        reservationId
    );

    if (updatedRows == 0) {
      throw new IllegalStateException(
          "No se encontró la reserva con id: " + reservationId
      );
    }
  }

  private String query(String key) {

    String sql = reservationQueries.getProperty(key);

    if (sql == null || sql.isBlank()) {
      throw new IllegalStateException(
          "No se encontró la query configurada: " + key
      );
    }

    return sql;
  }
}
