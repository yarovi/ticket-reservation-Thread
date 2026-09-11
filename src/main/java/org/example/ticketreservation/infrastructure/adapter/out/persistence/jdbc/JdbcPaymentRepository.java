package org.example.ticketreservation.infrastructure.adapter.out.persistence.jdbc;

import org.example.ticketreservation.application.port.out.PaymentRepository;
import org.example.ticketreservation.domain.enums.PaymentMethod;
import org.example.ticketreservation.domain.enums.PaymentStatus;
import org.example.ticketreservation.domain.model.Payment;
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
public class JdbcPaymentRepository implements PaymentRepository {

  private final JdbcTemplate jdbcTemplate;
  private final Properties paymentQueries;

  public JdbcPaymentRepository(
      JdbcTemplate jdbcTemplate,
      @Qualifier("paymentQueries") Properties paymentQueries
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.paymentQueries = paymentQueries;
  }

  private static final RowMapper<Payment> PAYMENT_ROW_MAPPER =
      new RowMapper<>() {

        @Override
        public Payment mapRow(ResultSet rs, int rowNum)
            throws SQLException {

          return new Payment(
              rs.getLong("id"),
              rs.getLong("reservation_id"),
              rs.getString("payment_code"),
              PaymentMethod.valueOf(
                  rs.getString("method")
              ),
              rs.getBigDecimal("amount"),
              PaymentStatus.valueOf(
                  rs.getString("status")
              ),
              rs.getTimestamp("created_at").toInstant()
          );
        }
      };

  @Override
  public Payment save(Payment payment) {

    if (payment.id() != null) {
      throw new IllegalArgumentException(
          "PaymentRepository.save solo crea nuevos pagos"
      );
    }

    String sql = query("payment.insert");

    Long generatedId = jdbcTemplate.queryForObject(
        sql,
        Long.class,
        payment.reservationId(),
        payment.paymentCode(),
        payment.method().name(),
        payment.amount(),
        payment.status().name(),
        Timestamp.from(payment.createdAt())
    );

    return new Payment(
        generatedId,
        payment.reservationId(),
        payment.paymentCode(),
        payment.method(),
        payment.amount(),
        payment.status(),
        payment.createdAt()
    );
  }

  @Override
  public Optional<Payment> findByPaymentCode(String paymentCode) {

    return jdbcTemplate.query(
            query("payment.find-by-payment-code"),
            PAYMENT_ROW_MAPPER,
            paymentCode
        )
        .stream()
        .findFirst();
  }

  private String query(String key) {

    String sql = paymentQueries.getProperty(key);

    if (sql == null || sql.isBlank()) {
      throw new IllegalStateException(
          "No se encontró la query configurada: " + key
      );
    }

    return sql;
  }
}
