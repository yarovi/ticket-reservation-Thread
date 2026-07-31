package org.example.application.port.out;

import org.example.domain.model.Reservation;
import org.example.domain.model.ReservationRequest;
import org.example.domain.result.ReservationResult;

import java.sql.Connection;

public interface ReservationRepository {
    Reservation save(Connection connection, Reservation reservation);
}
