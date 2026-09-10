package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.event.PassengerCheckedInEvent;

public interface CheckInEventPublisher {

  void publish(PassengerCheckedInEvent event);
}
