package org.example.ticketreservation.domain.exception;

public class CustomerNotFoundException extends RuntimeException {

  public CustomerNotFoundException(Long customerId) {
    super("No existe el cliente con id: " + customerId);
  }
}
