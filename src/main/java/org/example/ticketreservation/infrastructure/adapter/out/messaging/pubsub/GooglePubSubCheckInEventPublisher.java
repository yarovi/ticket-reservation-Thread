package org.example.ticketreservation.infrastructure.adapter.out.messaging.pushpub;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.example.ticketreservation.application.port.out.CheckInEventPublisher;
import org.example.ticketreservation.application.port.out.MetricsPublisher;
import org.example.ticketreservation.domain.event.PassengerCheckedInEvent;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class GooglePubSubCheckInEventPublisher
    implements CheckInEventPublisher {

  private final Publisher publisher;
  private final ObjectMapper objectMapper;
  private final MetricsPublisher metricsPublisher;

  public GooglePubSubCheckInEventPublisher(
      Publisher publisher,
      ObjectMapper objectMapper,
      MetricsPublisher metricsPublisher
  ) {
    this.publisher = publisher;
    this.objectMapper = objectMapper;
    this.metricsPublisher = metricsPublisher;
  }

  @Override
  public void publish(PassengerCheckedInEvent event) {

    String json = objectMapper.writeValueAsString(event);

    PubsubMessage message = PubsubMessage
        .newBuilder()
        .setData(
            ByteString.copyFromUtf8(json)
        )
        .build();

    publisher.publish(message);

    metricsPublisher.increment(
        "pubsub.checkin.published"
    );

  }
}
