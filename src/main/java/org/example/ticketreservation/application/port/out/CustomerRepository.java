package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.model.Customer;

import java.util.Optional;
import java.util.List;
public interface CustomerRepository {

  Customer save(Customer customer);

  Optional<Customer> findById(Long customerId);

  Optional<Customer> findByUniqueId(String uniqueId);

  List<Customer> findAll();

  boolean existsByEmail(String email);

  boolean existsByUniqueId(String uniqueId);

  void deleteById(Long customerId);

}
