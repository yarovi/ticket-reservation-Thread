package org.example.ticketreservation.application.service;

import org.example.ticketreservation.application.command.CreateCustomerCommand;
import org.example.ticketreservation.application.port.in.CreateCustomerUseCase;
import org.example.ticketreservation.application.port.out.CustomerRepository;
import org.example.ticketreservation.application.port.out.MetricsPublisher;
import org.example.ticketreservation.domain.model.Customer;

public class CustomerService implements CreateCustomerUseCase {
  private final CustomerRepository customerRepository;
  private final MetricsPublisher metricsPublisher;

  public CustomerService(
      CustomerRepository customerRepository,
      MetricsPublisher metricsPublisher
  ) {
    this.customerRepository = customerRepository;
    this.metricsPublisher = metricsPublisher;
  }

  @Override
  public Customer create(CreateCustomerCommand command) {

    if (customerRepository.existsByEmail(command.email())) {
      throw new IllegalArgumentException(
          "Ya existe un cliente registrado con el correo: "
              + command.email()
      );
    }

    var customer = new Customer(
        null,
        command.uniqueId(),
        command.name(),
        command.email()
    );

    var savedCustomer = customerRepository.save(customer);

    metricsPublisher.increment("customer.created");

    return savedCustomer;
  }
}
