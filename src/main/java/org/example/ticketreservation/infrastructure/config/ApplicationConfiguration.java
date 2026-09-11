package org.example.ticketreservation.infrastructure.config;


import org.example.ticketreservation.application.port.in.*;
import org.example.ticketreservation.application.port.out.*;
import org.example.ticketreservation.application.service.*;
import org.example.ticketreservation.domain.exception.ReservationExpiredException;
import org.example.ticketreservation.domain.model.Ticket;
import org.example.ticketreservation.infrastructure.ConfirmationTransactionResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public CreateCustomerUseCase createCustomerUseCase(
      CustomerRepository customerRepository,
      MetricsPublisher metricsPublisher
  ) {
    return new CustomerService(
        customerRepository,
        metricsPublisher
    );
  }

  @Bean
  public ReserveTicketUseCase reserveTicketUseCase(
      CustomerRepository customerRepository,
      TripRepository tripRepository,
      SeatRepository seatRepository,
      ReservationRepository reservationRepository,
      CodeGenerator codeGenerator,
      MetricsPublisher metricsPublisher,
      Clock clock,
      ReservationProperties reservationProperties,
      TransactionTemplate transactionTemplate
  ) {
    var service = new ReservationService(
        customerRepository,
        tripRepository,
        seatRepository,
        reservationRepository,
        codeGenerator,
        metricsPublisher,
        clock,
        reservationProperties.expiration()
    );

    return command ->
        transactionTemplate.execute(
            status -> service.reserve(command)
        );
  }

  @Bean
  public ConfirmReservationUseCase confirmReservationUseCase(
      ReservationRepository reservationRepository,
      PaymentRepository paymentRepository,
      TicketRepository ticketRepository,
      SeatRepository seatRepository,
      CodeGenerator codeGenerator,
      MetricsPublisher metricsPublisher,
      Clock clock,
      TransactionTemplate transactionTemplate
  ) {
    var service = new ConfirmationService(
        reservationRepository,
        paymentRepository,
        ticketRepository,
        seatRepository,
        codeGenerator,
        metricsPublisher,
        clock
    );

    return command -> {

      ConfirmationTransactionResult result =
          transactionTemplate.execute(status -> {

            try {

              Ticket ticket = service.confirm(command);

              return ConfirmationTransactionResult.success(
                  ticket
              );

            } catch (ReservationExpiredException exception) {

              /*
               * IMPORTANTE:
               *
               * No propagamos todavía la excepción.
               *
               * El servicio ya realizó:
               *
               * Reservation -> EXPIRED
               * Seat        -> AVAILABLE
               *
               * Queremos permitir que la transacción
               * haga COMMIT.
               */
              return ConfirmationTransactionResult.expired(
                  exception
              );
            }
          });

      if (result == null) {
        throw new IllegalStateException(
            "No fue posible ejecutar la confirmación"
        );
      }

      /*
       * En este punto la transacción ya terminó.
       *
       * Si la reserva había expirado,
       * ahora sí devolvemos el error de negocio.
       */
      if (result.expirationException() != null) {
        throw result.expirationException();
      }

      return result.ticket();
    };
  }

  @Bean
  public CancelReservationUseCase cancelReservationUseCase(
      ReservationRepository reservationRepository,
      SeatRepository seatRepository,
      MetricsPublisher metricsPublisher,
      TransactionTemplate transactionTemplate
  ) {
    var service = new CancelReservationService(
        reservationRepository,
        seatRepository,
        metricsPublisher
    );

    return reservationCode ->
        transactionTemplate.execute(
            status -> service.cancel(reservationCode)
        );
  }

  @Bean
  public SearchReservationUseCase searchReservationUseCase(
      ReservationRepository reservationRepository
  ) {
    return new SearchReservationService(
        reservationRepository
    );
  }

  @Bean
  public ListSeatsUseCase listSeatsUseCase(
      TripRepository tripRepository,
      SeatRepository seatRepository
  ) {
    return new ListSeatsService(
        tripRepository,
        seatRepository
    );
  }

  @Bean
  public CheckInUseCase checkInUseCase(
      ReservationRepository reservationRepository,
      TicketRepository ticketRepository,
      SeatRepository seatRepository,
      CheckInRepository checkInRepository,
      CheckInEventPublisher checkInEventPublisher,
      MetricsPublisher metricsPublisher,
      Clock clock
  ) {
    return new CheckInService(
        reservationRepository,
        ticketRepository,
        seatRepository,
        checkInRepository,
        checkInEventPublisher,
        metricsPublisher,
        clock
    );
  }

  @Bean
  public TransactionTemplate transactionTemplate(
      PlatformTransactionManager transactionManager
  ) {
    return new TransactionTemplate(transactionManager);
  }

}
