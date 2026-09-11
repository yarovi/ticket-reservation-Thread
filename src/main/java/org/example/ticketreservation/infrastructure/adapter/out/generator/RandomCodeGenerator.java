package org.example.ticketreservation.infrastructure.adapter.out.generator;

import lombok.extern.apachecommons.CommonsLog;
import org.example.ticketreservation.application.port.out.CodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomCodeGenerator implements CodeGenerator {

  private static final String ALPHANUMERIC =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  private static final int RESERVATION_CODE_LENGTH = 12;
  private static final int PAYMENT_CODE_LENGTH = 10;
  private static final int TICKET_CODE_LENGTH = 12;

  private final SecureRandom random = new SecureRandom();

  @Override
  public String reservationCode() {
    return generate(RESERVATION_CODE_LENGTH);
  }

  @Override
  public String paymentCode() {
    return generate(PAYMENT_CODE_LENGTH);
  }

  @Override
  public String ticketCode() {
    return generate(TICKET_CODE_LENGTH);
  }

  private String generate(int length) {

    StringBuilder value = new StringBuilder(length);

    for (int i = 0; i < length; i++) {

      int index = random.nextInt(ALPHANUMERIC.length());

      value.append(
          ALPHANUMERIC.charAt(index)
      );
    }

    return value.toString();
  }
}
