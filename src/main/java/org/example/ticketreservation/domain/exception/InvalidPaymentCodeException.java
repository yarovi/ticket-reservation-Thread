package org.example.ticketreservation.domain.exception;

public class InvalidPaymentCodeException extends RuntimeException{

    public InvalidPaymentCodeException() {
      super("El código de pago proporcionado no es válido");
    }
}
