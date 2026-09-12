package org.example.ticketreservation.infrastructure.config;

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


}
