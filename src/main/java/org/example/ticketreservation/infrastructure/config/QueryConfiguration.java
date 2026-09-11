package org.example.ticketreservation.infrastructure.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class QueryConfiguration {
  @Bean("customerQueries")
  public Properties customerQueries() throws IOException {

    PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();

    factoryBean.setLocation(
        new ClassPathResource(
            "queries/customer-queries.properties"
        )
    );

    factoryBean.afterPropertiesSet();

    Properties properties = factoryBean.getObject();

    if (properties == null) {
      throw new IllegalStateException(
          "No se pudieron cargar las queries de Customer"
      );
    }

    return properties;
  }
}
