package org.example.application.port.in;

import org.example.domain.model.ReservationRequest;
import org.example.domain.result.ReservationResult;

import java.sql.SQLException;

public interface ReserveTicketUseCase {
    ReservationResult reserve(
            ReservationRequest request
    ) throws SQLException;
}
