package org.example.ticketreservation.application.port.out;

import org.example.ticketreservation.domain.model.Payment;

import java.util.Optional;

public interface PaymentRepository {

  Payment save(Payment payment);

  Optional<Payment> findByPaymentCode(String paymentCode);
}
