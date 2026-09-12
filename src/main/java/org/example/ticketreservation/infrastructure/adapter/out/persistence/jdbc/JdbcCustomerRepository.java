package org.example.ticketreservation.infrastructure.adapter.out.persistence.jdbc;


import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Repository
public class JdbcCustomerRepository implements CustomerRepository {

  private final JdbcTemplate jdbcTemplate;
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

  public JdbcCustomerRepository(
      JdbcTemplate jdbcTemplate,
      @Qualifier("customerQueries") Properties customerQueries
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.customerQueries = customerQueries;

    log.info(
        "JdbcCustomerRepository inicializado. Queries disponibles: {}",
        customerQueries.stringPropertyNames()
    );
  }

  @Override
  public Customer save(Customer customer) {

    log.debug(
        "Guardando customer. id={}, uniqueId={}, email={}",
        customer.id(),
        customer.uniqueId(),
        customer.email()
    );

    if (customer.id() == null) {
      return insert(customer);
    }

    return update(customer);
  }

  @Override
  public Optional<Customer> findById(Long customerId) {

    log.debug("Buscando customer por id={}", customerId);

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

    log.debug("Buscando customer por uniqueId={}", uniqueId);

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

    log.debug("Listando todos los customers");

    String sql = query("customer.find-all");

    return jdbcTemplate.query(
        sql,
        CUSTOMER_ROW_MAPPER
    );
  }

  @Override
  public boolean existsByEmail(String email) {

    log.debug("Validando existencia de customer por email={}", email);

    String sql = query("customer.exists-by-email");

    Integer count = jdbcTemplate.queryForObject(
        sql,
        Integer.class,
        email
    );

    boolean exists = count != null && count > 0;

    log.debug(
        "Resultado existsByEmail para email={}: {}",
        email,
        exists
    );

    return exists;
  }

  @Override
  public boolean existsByUniqueId(String uniqueId) {

    log.debug(
        "Validando existencia de customer por uniqueId={}",
        uniqueId
    );

    String sql = query("customer.exists-by-unique-id");

    Integer count = jdbcTemplate.queryForObject(
        sql,
        Integer.class,
        uniqueId
    );

    boolean exists = count != null && count > 0;

    log.debug(
        "Resultado existsByUniqueId para uniqueId={}: {}",
        uniqueId,
        exists
    );

    return exists;
  }

  @Override
  public void deleteById(Long customerId) {

    log.info("Eliminando customer id={}", customerId);

    String sql = query("customer.delete-by-id");

    int deletedRows = jdbcTemplate.update(
        sql,
        customerId
    );

    log.info(
        "Customer delete finalizado. id={}, filas afectadas={}",
        customerId,
        deletedRows
    );
  }

  private Customer insert(Customer customer) {

    log.info(
        "Insertando customer uniqueId={}, email={}",
        customer.uniqueId(),
        customer.email()
    );

    String sql = query("customer.insert");

    Long generatedId = jdbcTemplate.queryForObject(
        sql,
        Long.class,
        customer.uniqueId(),
        customer.name(),
        customer.email()
    );

    log.info(
        "Customer insertado correctamente. id={}, uniqueId={}",
        generatedId,
        customer.uniqueId()
    );

    return new Customer(
        generatedId,
        customer.uniqueId(),
        customer.name(),
        customer.email()
    );
  }

  private Customer update(Customer customer) {

    log.info("Actualizando customer id={}", customer.id());

    String sql = query("customer.update");

    int updatedRows = jdbcTemplate.update(
        sql,
        customer.uniqueId(),
        customer.name(),
        customer.email(),
        customer.id()
    );

    if (updatedRows == 0) {

      log.warn(
          "No se encontró customer para actualizar. id={}",
          customer.id()
      );

      throw new IllegalStateException(
          "No se encontró el cliente con id: " + customer.id()
      );
    }

    log.info(
        "Customer actualizado correctamente. id={}",
        customer.id()
    );

    return customer;
  }

  private String query(String key) {

    log.debug(
        "Buscando query key='{}'. Queries disponibles={}",
        key,
        customerQueries.stringPropertyNames()
    );

    String sql = customerQueries.getProperty(key);

    if (sql == null || sql.isBlank()) {

      log.error(
          "No se encontró query key='{}'. Queries cargadas={}",
          key,
          customerQueries.stringPropertyNames()
      );

      throw new IllegalStateException(
          "No se encontró la query configurada: " + key
      );
    }

    log.trace(
        "Query encontrada. key='{}', sql='{}'",
        key,
        sql
    );

    return sql;
  }
}
