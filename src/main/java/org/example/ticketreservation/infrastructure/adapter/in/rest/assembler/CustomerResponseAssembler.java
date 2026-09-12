package org.example.ticketreservation.infrastructure.adapter.in.rest.assembler;

import org.example.ticketreservation.domain.model.Customer;
import org.example.ticketreservation.infrastructure.adapter.in.rest.CustomerController;
import org.example.ticketreservation.infrastructure.adapter.in.rest.response.CustomerResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CustomerResponseAssembler {
  public EntityModel<CustomerResponse> toModel(
      Customer customer
  ) {

    var response = new CustomerResponse(
        customer.id(),
        customer.uniqueId(),
        customer.name(),
        customer.email()
    );

    var model = EntityModel.of(response);

    model.add(
        linkTo(
            methodOn(CustomerController.class)
                .findById(customer.id())
        ).withSelfRel()
    );

    model.add(
        linkTo(
            methodOn(CustomerController.class)
                .findAll()
        ).withRel("customers")
    );

    return model;
  }

}
