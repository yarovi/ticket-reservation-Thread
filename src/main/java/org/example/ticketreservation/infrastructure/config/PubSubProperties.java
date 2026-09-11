package org.example.ticketreservation.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pubsub")
public record PubSubProperties(
    String projectId,
    String topicId
) {
}
