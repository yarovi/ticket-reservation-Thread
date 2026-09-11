package org.example.ticketreservation.infrastructure.config;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PubSubConfiguration {

  @Bean(destroyMethod = "shutdown")
  public Publisher checkInPublisher(
      PubSubProperties properties
  ) throws IOException {

    TopicName topicName = TopicName.of(
        properties.projectId(),
        properties.topicId()
    );

    return Publisher
        .newBuilder(topicName)
        .build();
  }
}
