package org.example.ticketreservation.infrastructure.adapter.in.rest;

import org.example.ticketreservation.application.command.CreateCustomerCommand;
import org.example.ticketreservation.application.port.in.CreateCustomerUseCase;
import org.example.ticketreservation.application.port.in.ManageCustomerUseCase;
import org.example.ticketreservation.domain.model.Customer;
import org.example.ticketreservation.infrastructure.adapter.in.rest.assembler.CustomerResponseAssembler;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.CreateCustomerRequest;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.UpdateCustomerRequest;
import org.example.ticketreservation.infrastructure.adapter.in.rest.response.CustomerResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

  private final CreateCustomerUseCase createCustomerUseCase;
  private final ManageCustomerUseCase manageCustomerUseCase;
  private final CustomerResponseAssembler assembler;

  public CustomerController(
      CreateCustomerUseCase createCustomerUseCase,
      ManageCustomerUseCase manageCustomerUseCase, CustomerResponseAssembler assembler
  ) {
    this.createCustomerUseCase = createCustomerUseCase;
    this.manageCustomerUseCase = manageCustomerUseCase;
    this.assembler = assembler;
  }
/*
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
  */

  @PostMapping
  public ResponseEntity<EntityModel<CustomerResponse>> create(
      @RequestBody CreateCustomerRequest request
  ) {

    var command = new CreateCustomerCommand(
        request.uniqueId(),
        request.name(),
        request.email()
    );

    var customer =
        createCustomerUseCase.create(command);

    var response =
        assembler.toModel(customer);

    var location = URI.create(
        "/api/v1/customers/" + customer.id()
    );

    return ResponseEntity
        .created(location)
        .body(response);
  }

  @GetMapping("/{customerId}")
  public ResponseEntity<EntityModel<CustomerResponse>> findById(
      @PathVariable Long customerId
  ) {

    return manageCustomerUseCase
        .findById(customerId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(
            () -> ResponseEntity.notFound().build()
        );
  }

  @GetMapping
  public ResponseEntity<
      CollectionModel<EntityModel<CustomerResponse>>
      > findAll() {

    var customers =
        manageCustomerUseCase.findAll();

    var responses = customers.stream()
        .map(assembler::toModel)
        .toList();

    var collection =
        CollectionModel.of(responses);

    collection.add(
        linkTo(
            methodOn(CustomerController.class)
                .findAll()
        ).withSelfRel()
    );

    return ResponseEntity.ok(collection);
  }

  @PutMapping("/{customerId}")
  public ResponseEntity<EntityModel<CustomerResponse>> update(
      @PathVariable Long customerId,
      @RequestBody UpdateCustomerRequest request
  ) {

    var customer = new Customer(
        customerId,
        request.uniqueId(),
        request.name(),
        request.email()
    );

    var updated =
        manageCustomerUseCase.update(
            customerId,
            customer
        );

    return ResponseEntity.ok(
        assembler.toModel(updated)
    );
  }

  @DeleteMapping("/{customerId}")
  public ResponseEntity<Void> delete(
      @PathVariable Long customerId
  ) {

    manageCustomerUseCase.delete(customerId);

    return ResponseEntity.noContent().build();
  }


}
