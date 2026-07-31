package org.example.boostrap;

import org.example.application.port.in.MetricsPublisher;
import org.example.application.port.out.ReservationRepository;
import org.example.application.port.out.TicketRepository;
import org.example.application.service.ReservationService;
import org.example.infraestructure.DatabaseConfiguration;
import org.example.infraestructure.config.ConnectionFactory;
import org.example.infraestructure.database.JdbcTicketRepository;
import org.example.infraestructure.metric.ConsoleMetricsPublisher;
import org.example.infraestructure.repository.JdbcReservationRepository;

import javax.sql.DataSource;

public class ApplicationConfiguration {

    private final TicketRepository ticketRepository;

    private final ReservationRepository reservationRepository;

    private final MetricsPublisher metricsPublisher;

    private final ReservationService reservationService;

    private final ConnectionFactory connectionFactory;


    public ApplicationConfiguration(ConnectionFactory connectionFactory) {

        this.connectionFactory = connectionFactory;

        this.ticketRepository =
                new JdbcTicketRepository(connectionFactory);

        this.reservationRepository =
                new JdbcReservationRepository(connectionFactory);

        this.metricsPublisher =
                new ConsoleMetricsPublisher();

        this.reservationService =
                new ReservationService(
                        ticketRepository,
                        reservationRepository,
                        metricsPublisher,
                        connectionFactory
                );
    }


    public ReservationService reservationService() {
        return reservationService;
    }
}