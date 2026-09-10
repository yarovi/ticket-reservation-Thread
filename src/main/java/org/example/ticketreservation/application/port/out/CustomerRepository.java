package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.model.Customer;

import java.util.Optional;

public interface CustomerRepository {

  Customer save(Customer customer);

  Optional<Customer> findById(Long customerId);

  Optional<Customer> findByUniqueId(String uniqueId);

  boolean existsByEmail(String email);

}
