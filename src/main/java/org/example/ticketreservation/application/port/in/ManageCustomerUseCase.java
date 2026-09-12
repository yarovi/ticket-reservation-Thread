package org.example.ticketreservation.application.port.in;

import org.example.ticketreservation.domain.model.Customer;
import java.util.List;
import java.util.Optional;

public interface ManageCustomerUseCase {
  Optional<Customer> findById(Long customerId);

  List<Customer> findAll();

  Customer update(Long customerId, Customer customer);

  void delete(Long customerId);
}
