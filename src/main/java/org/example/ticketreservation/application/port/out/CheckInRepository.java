package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.model.CheckIn;

import java.util.Optional;

public interface CheckInRepository {

  CheckIn save(CheckIn checkIn);

  Optional<CheckIn> findByReservationCode(String reservationCode);
}
