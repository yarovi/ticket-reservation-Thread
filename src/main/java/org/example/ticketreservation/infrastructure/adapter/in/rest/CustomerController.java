package org.example.ticketreservation.infrastructure.adapter.in.rest;

import org.example.ticketreservation.application.command.CreateCustomerCommand;
import org.example.ticketreservation.application.port.in.CreateCustomerUseCase;
import org.example.ticketreservation.application.port.in.ManageCustomerUseCase;
import org.example.ticketreservation.domain.model.Customer;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.CreateCustomerRequest;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.UpdateCustomerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

  private final CreateCustomerUseCase createCustomerUseCase;
  private final ManageCustomerUseCase manageCustomerUseCase;

  public CustomerController(
      CreateCustomerUseCase createCustomerUseCase,
      ManageCustomerUseCase manageCustomerUseCase
  ) {
    this.createCustomerUseCase = createCustomerUseCase;
    this.manageCustomerUseCase = manageCustomerUseCase;
  }

  @PostMapping
  public ResponseEntity<Customer> create(
      @RequestBody CreateCustomerRequest request
  ) {

    var command = new CreateCustomerCommand(
        request.uniqueId(),
        request.name(),
        request.email()
    );

    Customer customer =
        createCustomerUseCase.create(command);

    URI location = URI.create(
        "/api/v1/customers/" + customer.id()
    );

    return ResponseEntity
        .created(location)
        .body(customer);
  }

  @GetMapping
  public ResponseEntity<List<Customer>> findAll() {
    return ResponseEntity.ok(
        manageCustomerUseCase.findAll()
    );
  }

  @GetMapping("/{customerId}")
  public ResponseEntity<Customer> findById(
      @PathVariable Long customerId
  ) {

    return manageCustomerUseCase
        .findById(customerId)
        .map(ResponseEntity::ok)
        .orElseGet(
            () -> ResponseEntity.notFound().build()
        );
  }

  @PutMapping("/{customerId}")
  public ResponseEntity<Customer> update(
      @PathVariable Long customerId,
      @RequestBody UpdateCustomerRequest request
  ) {

    Customer customer = new Customer(
        customerId,
        request.uniqueId(),
        request.name(),
        request.email()
    );

    Customer updated =
        manageCustomerUseCase.update(
            customerId,
            customer
        );

    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{customerId}")
  public ResponseEntity<Void> delete(
      @PathVariable Long customerId
  ) {

    manageCustomerUseCase.delete(customerId);

    return ResponseEntity.noContent().build();
  }
}
