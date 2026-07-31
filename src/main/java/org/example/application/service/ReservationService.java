package org.example.application.service;

import org.example.application.port.in.MetricsPublisher;
import org.example.application.port.out.ReservationRepository;
import org.example.application.port.in.ReserveTicketUseCase;
import org.example.application.port.out.TicketRepository;
import org.example.domain.enums.TicketStatus;
import org.example.domain.model.Reservation;
import org.example.domain.model.ReservationRequest;
import org.example.domain.model.Ticket;
import org.example.domain.result.ReservationRejected;
import org.example.domain.result.ReservationResult;
import org.example.domain.result.ReservationSuccess;
import org.example.infraestructure.config.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

public class ReservationService implements ReserveTicketUseCase {

    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final MetricsPublisher metricsPublisher;
    private final ConnectionFactory connectionFactory;

    public ReservationService(
            TicketRepository ticketRepository,
            ReservationRepository reservationRepository,
            MetricsPublisher metricsPublisher,
            ConnectionFactory connectionFactory) {

        this.ticketRepository = ticketRepository;
        this.reservationRepository = reservationRepository;
        this.metricsPublisher = metricsPublisher;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ReservationResult reserve(ReservationRequest request) throws SQLException {
        Connection con = connectionFactory.getConnection();
        con.setAutoCommit(false);
        Optional<Ticket> ticketOptional =
                ticketRepository.findBySeat(con, request.seat());
        if (ticketOptional.isEmpty()) {

            ReservationRejected rejected =
                    new ReservationRejected("Seat not found");
            con.rollback();

            metricsPublisher.publish(rejected);

            return rejected;
        }
        Ticket ticket = ticketOptional.get();
        if (ticket.status() != TicketStatus.AVAILABLE) {

            ReservationRejected rejected =
                    new ReservationRejected("Seat already reserved");
            con.rollback();
            metricsPublisher.publish(rejected);

            return rejected;
        }
        Reservation reservation =
                new Reservation(
                        0L,
                        request.customerId(),
                        ticket.id(),
                        Instant.now()
                );
        Reservation savedReservation =
                reservationRepository.save(con, reservation);
        Ticket reservedTicket =
                new Ticket(
                        ticket.id(),
                        ticket.seat(),
                        TicketStatus.RESERVED
                );

        ticketRepository.update(con,reservedTicket);

        ReservationSuccess success =
                new ReservationSuccess(savedReservation.id());
        con.commit();
        metricsPublisher.publish(success);
        con.close();
        return success;
    }
}
