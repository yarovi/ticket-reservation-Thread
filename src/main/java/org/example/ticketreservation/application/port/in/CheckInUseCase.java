package org.example.ticketreservation.application.port.in;

import org.example.ticketreservation.application.command.CheckInCommand;
import org.example.ticketreservation.domain.model.CheckIn;

public interface CheckInUseCase {
  CheckIn checkIn(CheckInCommand command);
}
