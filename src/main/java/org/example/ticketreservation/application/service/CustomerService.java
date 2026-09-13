package org.example.ticketreservation.application.service;

import lombok.extern.slf4j.Slf4j;
import org.example.ticketreservation.application.command.CreateCustomerCommand;
import org.example.ticketreservation.application.port.in.CreateCustomerUseCase;
import org.example.ticketreservation.application.port.in.ManageCustomerUseCase;
import org.example.ticketreservation.application.port.out.CustomerRepository;
import org.example.ticketreservation.application.port.out.MetricsPublisher;
import org.example.ticketreservation.domain.exception.CustomerNotFoundException;
import org.example.ticketreservation.domain.model.Customer;

import java.util.List;
import java.util.Optional;
@Slf4j
public class CustomerService implements CreateCustomerUseCase, ManageCustomerUseCase {
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

  @Override
  public Optional<Customer> findById(Long customerId) {
    return  customerRepository.findById(customerId);
  }

  @Override
  public List<Customer> findAll() {
    log.debug("Consultando todos los customers");

    return customerRepository.findAll();
  }

  @Override
  public Customer update(Long customerId, Customer customer) {
    log.info(
        "Iniciando actualización de customer id={}",
        customerId
    );

    Customer currentCustomer = customerRepository
        .findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(customerId));

    /*
     * Solo comprobamos duplicidad cuando el email realmente cambió.
     * Esto evita rechazar una actualización donde el cliente conserva
     * su propio email.
     */
    if (!currentCustomer.email().equalsIgnoreCase(customer.email())) {
      validateEmailNotRegistered(customer.email());
    }

    /*
     * Lo mismo para uniqueId. Si no cambió, pertenece al mismo customer
     * y no debe considerarse duplicado.
     */
    if (!currentCustomer.uniqueId().equals(customer.uniqueId())) {
      validateUniqueIdNotRegistered(customer.uniqueId());
    }

    Customer customerToUpdate = new Customer(
        customerId,
        customer.uniqueId(),
        customer.name(),
        customer.email()
    );

    Customer updatedCustomer =
        customerRepository.save(customerToUpdate);

    log.info(
        "Customer actualizado correctamente. id={}",
        updatedCustomer.id()
    );

    return updatedCustomer;
  }

  @Override
  public void delete(Long customerId) {
    log.info(
        "Iniciando eliminación de customer id={}",
        customerId
    );

    customerRepository
        .findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(customerId));

    customerRepository.deleteById(customerId);

    log.info(
        "Customer eliminado correctamente. id={}",
        customerId
    );


  }

  private void validateEmailNotRegistered(String email) {

    log.debug(
        "Validando que email={} no esté registrado",
        email
    );

    if (customerRepository.existsByEmail(email)) {

      log.warn(
          "Intento de registrar email duplicado={}",
          email
      );

      throw new IllegalArgumentException(
          "Ya existe un cliente registrado con el email: " + email
      );
    }
  }

  private void validateUniqueIdNotRegistered(String uniqueId) {

    log.debug(
        "Validando que uniqueId={} no esté registrado",
        uniqueId
    );

    if (customerRepository.existsByUniqueId(uniqueId)) {

      log.warn(
          "Intento de registrar uniqueId duplicado={}",
          uniqueId
      );

      throw new IllegalArgumentException(
          "Ya existe un cliente registrado con el identificador: " + uniqueId
      );
    }
  }
}
