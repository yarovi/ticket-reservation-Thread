package org.example.ticketreservation.application.port.out;

public interface CodeGenerator {

  String reservationCode();

  String paymentCode();

  String ticketCode();
}
