package org.example.ticketreservation.infrastructure.adapter.out.persistence.jdbc;


import org.example.ticketreservation.application.port.out.CustomerRepository;
import org.example.ticketreservation.domain.model.Customer;
import org.example.ticketreservation.infrastructure.config.QueryProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


@Repository
public class JdbcCustomerRepository implements CustomerRepository {

  private final JdbcTemplate jdbcTemplate;
  @Qualifier("customerQueries")
  private final Properties customerQueries;

  private static final RowMapper<Customer> CUSTOMER_ROW_MAPPER =
      new RowMapper<>() {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new Customer(
              rs.getLong("id"),
              rs.getString("unique_id"),
              rs.getString("name"),
              rs.getString("email")
          );
        }
      };

  public JdbcCustomerRepository(JdbcTemplate jdbcTemplate, Properties customerQueries) {
    this.jdbcTemplate = jdbcTemplate;
    this.customerQueries = customerQueries;
  }


  @Override
  public Customer save(Customer customer) {
    if (customer.id() == null) {
      return insert(customer);
    }

    return update(customer);
  }

  @Override
  public Optional<Customer> findById(Long customerId) {
    String sql = query("customer.find-by-id");

    return jdbcTemplate.query(
            sql,
            CUSTOMER_ROW_MAPPER,
            customerId
        )
        .stream()
        .findFirst();
  }

  @Override
  public Optional<Customer> findByUniqueId(String uniqueId) {
    String sql = query("customer.find-by-unique-id");

    return jdbcTemplate.query(
            sql,
            CUSTOMER_ROW_MAPPER,
            uniqueId
        )
        .stream()
        .findFirst();
  }

  @Override
  public List<Customer> findAll() {
    String sql = query("customer.find-all");

    return jdbcTemplate.query(
        sql,
        CUSTOMER_ROW_MAPPER
    );
  }

  @Override
  public boolean existsByEmail(String email) {
    String sql = query("customer.exists-by-email");

    Integer count = jdbcTemplate.queryForObject(
        sql,
        Integer.class,
        email
    );

    return count != null && count > 0;
  }

  @Override
  public boolean existsByUniqueId(String uniqueId) {
    String sql = query("customer.exists-by-unique-id");

    Integer count = jdbcTemplate.queryForObject(
        sql,
        Integer.class,
        uniqueId
    );

    return count != null && count > 0;
  }

  @Override
  public void deleteById(Long customerId) {
    String sql = query("customer.delete-by-id");

    jdbcTemplate.update(
        sql,
        customerId
    );
  }

  private Customer insert(Customer customer) {

    String sql = query("customer.insert");

    Long generatedId = jdbcTemplate.queryForObject(
        sql,
        Long.class,
        customer.uniqueId(),
        customer.name(),
        customer.email()
    );

    return new Customer(
        generatedId,
        customer.uniqueId(),
        customer.name(),
        customer.email()
    );
  }

  private Customer update(Customer customer) {

    String sql = query("customer.update");

    int updatedRows = jdbcTemplate.update(
        sql,
        customer.uniqueId(),
        customer.name(),
        customer.email(),
        customer.id()
    );

    if (updatedRows == 0) {
      throw new IllegalStateException(
          "No se encontró el cliente con id: " + customer.id()
      );
    }

    return customer;
  }

  private String query(String key) {

    String sql = customerQueries.getProperty(key);

    if (sql == null || sql.isBlank()) {
      throw new IllegalStateException(
          "No se encontró la query configurada: " + key
      );
    }

    return sql;
  }
}
