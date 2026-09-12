package org.example.ticketreservation.infrastructure.adapter.in.rest;

import org.example.ticketreservation.application.command.CheckInCommand;
import org.example.ticketreservation.application.port.in.CheckInUseCase;
import org.example.ticketreservation.domain.model.CheckIn;
import org.example.ticketreservation.infrastructure.adapter.in.rest.request.CheckInRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/check-ins")
public class CheckInController {

  private final CheckInUseCase checkInUseCase;

  public CheckInController(
      CheckInUseCase checkInUseCase
  ) {
    this.checkInUseCase = checkInUseCase;
  }

  @PostMapping
  public ResponseEntity<CheckIn> checkIn(
      @RequestBody CheckInRequest request
  ) {

    var command = new CheckInCommand(
        request.reservationCode()
    );

    CheckIn checkIn =
        checkInUseCase.checkIn(command);

    return ResponseEntity.ok(checkIn);
  }
}