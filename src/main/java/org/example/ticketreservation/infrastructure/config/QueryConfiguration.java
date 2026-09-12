package org.example.ticketreservation.infrastructure.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class QueryConfiguration {
  @Bean("customerQueries")
  public Properties customerQueries() throws IOException {
    return loadProperties(
        "queries/customer-queries.properties",
        "Customer"
    );
  }

  @Bean("tripQueries")
  public Properties tripQueries() throws IOException {
    return loadProperties(
        "queries/trip-queries.properties",
        "Trip"
    );
  }

  @Bean("seatQueries")
  public Properties seatQueries() throws IOException {
    return loadProperties(
        "queries/seat-queries.properties",
        "Seat"
    );
  }

  @Bean("reservationQueries")
  public Properties reservationQueries() throws IOException {
    return loadProperties(
        "queries/reservation-queries.properties",
        "Reservation"
    );
  }

  @Bean("paymentQueries")
  public Properties paymentQueries() throws IOException {
    return loadProperties(
        "queries/payment-queries.properties",
        "Payment"
    );
  }

  @Bean("ticketQueries")
  public Properties ticketQueries() throws IOException {
    return loadProperties(
        "queries/ticket-queries.properties",
        "Ticket"
    );
  }

  private Properties loadProperties(
      String path,
      String context
  ) throws IOException {

    PropertiesFactoryBean factoryBean =
        new PropertiesFactoryBean();

    factoryBean.setLocation(
        new ClassPathResource(path)
    );

    factoryBean.afterPropertiesSet();

    Properties properties =
        factoryBean.getObject();

    if (properties == null) {
      throw new IllegalStateException(
          "No se pudieron cargar las queries de " + context
      );
    }

    return properties;
  }
}
