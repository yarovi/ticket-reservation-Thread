package org.example.ticketreservation.application.port.in;

import org.example.ticketreservation.application.command.CreateCustomerCommand;
import org.example.ticketreservation.domain.model.Customer;

public interface CreateCustomerUseCase {
  Customer create(CreateCustomerCommand command);
}
