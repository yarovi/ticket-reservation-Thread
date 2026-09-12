package org.example.ticketreservation.infrastructure.adapter.in.rest;

import org.example.ticketreservation.application.port.in.ListSeatsUseCase;
import org.example.ticketreservation.domain.model.Seat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("/api/v1/trips")
public class SeatController {

  private final ListSeatsUseCase listSeatsUseCase;

  public SeatController(ListSeatsUseCase listSeatsUseCase) {
    this.listSeatsUseCase = listSeatsUseCase;
  }

  @GetMapping("/{tripId}/seats")
  public ResponseEntity<List<Seat>> findSeatsByTrip(
      @PathVariable Long tripId
  ) {

    List<Seat> seats =
        listSeatsUseCase.findByTrip(tripId);

    return ResponseEntity.ok(seats);
  }
}
