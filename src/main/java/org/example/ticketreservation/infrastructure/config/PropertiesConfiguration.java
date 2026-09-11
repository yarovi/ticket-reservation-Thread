package org.example.ticketreservation.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(
    ReservationProperties.class
)
public class PropertiesConfiguration {
}
