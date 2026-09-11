package org.example.ticketreservation.infrastructure.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class QueryProperties {
  private final Properties properties = new Properties();

  public QueryProperties(String path) {
    try (InputStream inputStream =
             new ClassPathResource(path).getInputStream()) {

      properties.load(inputStream);

    } catch (IOException e) {
      throw new IllegalStateException(
          "No se pudo cargar el archivo de queries: " + path,
          e
      );
    }
  }

  public String get(String key) {

    var query = properties.getProperty(key);

    if (query == null || query.isBlank()) {
      throw new IllegalArgumentException(
          "No existe la query configurada: " + key
      );
    }

    return query;
  }

  //TripQuery
  @Bean("tripQueries")
  public Properties tripQueries() throws IOException {

    PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();

    factoryBean.setLocation(
        new ClassPathResource(
            "queries/trip-queries.properties"
        )
    );

    factoryBean.afterPropertiesSet();

    Properties properties = factoryBean.getObject();

    if (properties == null) {
      throw new IllegalStateException(
          "No se pudieron cargar las queries de Trip"
      );
    }

    return properties;
  }

  //SeatingQuery
  @Bean("seatQueries")
  public Properties seatQueries() throws IOException {

    PropertiesFactoryBean factoryBean =
        new PropertiesFactoryBean();

    factoryBean.setLocation(
        new ClassPathResource(
            "queries/seat-queries.properties"
        )
    );

    factoryBean.afterPropertiesSet();

    Properties properties = factoryBean.getObject();

    if (properties == null) {
      throw new IllegalStateException(
          "No se pudieron cargar las queries de Seat"
      );
    }

    return properties;
  }

  //ReservationQuery
  @Bean("reservationQueries")
  public Properties reservationQueries() throws IOException {

    PropertiesFactoryBean factoryBean =
        new PropertiesFactoryBean();

    factoryBean.setLocation(
        new ClassPathResource(
            "queries/reservation-queries.properties"
        )
    );

    factoryBean.afterPropertiesSet();

    Properties properties = factoryBean.getObject();

    if (properties == null) {
      throw new IllegalStateException(
          "No se pudieron cargar las queries de Reservation"
      );
    }

    return properties;
  }

  //TicketQuery
  @Bean("ticketQueries")
  public Properties ticketQueries() throws IOException {

    PropertiesFactoryBean factoryBean =
        new PropertiesFactoryBean();

    factoryBean.setLocation(
        new ClassPathResource(
            "queries/ticket-queries.properties"
        )
    );

    factoryBean.afterPropertiesSet();

    Properties properties = factoryBean.getObject();

    if (properties == null) {
      throw new IllegalStateException(
          "No se pudieron cargar las queries de Ticket"
      );
    }

    return properties;
  }

}
