package org.example.ticketreservation.infrastructure.config;


import org.example.ticketreservation.application.port.in.*;
import org.example.ticketreservation.application.port.out.*;
import org.example.ticketreservation.application.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
      ReservationProperties reservationProperties
  ) {
    return new ReservationService(
        customerRepository,
        tripRepository,
        seatRepository,
        reservationRepository,
        codeGenerator,
        metricsPublisher,
        clock,
        reservationProperties.expiration()
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
      Clock clock
  ) {
    return new ConfirmationService(
        reservationRepository,
        paymentRepository,
        ticketRepository,
        seatRepository,
        codeGenerator,
        metricsPublisher,
        clock
    );
  }

  @Bean
  public CancelReservationUseCase cancelReservationUseCase(
      ReservationRepository reservationRepository,
      SeatRepository seatRepository,
      MetricsPublisher metricsPublisher
  ) {
    return new CancelReservationService(
        reservationRepository,
        seatRepository,
        metricsPublisher
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


}
