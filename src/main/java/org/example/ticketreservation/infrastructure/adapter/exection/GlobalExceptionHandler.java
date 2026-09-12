package org.example.ticketreservation.infrastructure.adapter.exection;

import org.example.ticketreservation.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
      CustomerNotFoundException.class,
      TripNotFoundException.class,
      SeatNotFoundException.class,
      ReservationNotFoundException.class,
      TicketNotFoundException.class
  })
  public ProblemDetail handleNotFound(RuntimeException exception) {

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            exception.getMessage()
        );

    problem.setTitle("Recurso no encontrado");

    return problem;
  }

  @ExceptionHandler({
      SeatNotAvailableException.class,
      ReservationExpiredException.class,
      ReservationCannotBeCancelledException.class,
      ReservationNotConfirmedException.class,
      PassengerAlreadyCheckedInException.class
  })
  public ProblemDetail handleConflict(RuntimeException exception) {

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            exception.getMessage()
        );

    problem.setTitle("Conflicto de negocio");

    return problem;
  }

  @ExceptionHandler({
      InvalidPaymentCodeException.class,
      InvalidSeatForTripException.class,
      IllegalArgumentException.class
  })
  public ProblemDetail handleBadRequest(RuntimeException exception) {

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
        );

    problem.setTitle("Solicitud inválida");

    return problem;
  }
}
