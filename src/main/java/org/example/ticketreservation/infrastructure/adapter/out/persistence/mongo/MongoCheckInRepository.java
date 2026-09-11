package org.example.ticketreservation.infrastructure.adapter.out.persistence.mongo;

import org.bson.Document;
import org.example.ticketreservation.application.port.out.CheckInRepository;
import org.example.ticketreservation.domain.model.CheckIn;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class MongoCheckInRepository implements CheckInRepository {

  private static final String COLLECTION_NAME = "check_ins";

  private final MongoTemplate mongoTemplate;

  public MongoCheckInRepository(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public CheckIn save(CheckIn checkIn) {

    Document document = new Document()
        .append("reservationCode", checkIn.reservationCode())
        .append("ticketCode", checkIn.ticketCode())
        .append("customerId", checkIn.customerId())
        .append("tripId", checkIn.tripId())
        .append("seatNumber", checkIn.seatNumber())
        .append("checkedIn", checkIn.checkedIn())
        .append("checkedAt", checkIn.checkedAt());

    Document savedDocument =
        mongoTemplate.insert(document, COLLECTION_NAME);

    return toDomain(savedDocument);
  }

  @Override
  public Optional<CheckIn> findByReservationCode(
      String reservationCode
  ) {

    var query = new org.springframework.data.mongodb.core.query.Query(
        org.springframework.data.mongodb.core.query.Criteria
            .where("reservationCode")
            .is(reservationCode)
    );

    Document document =
        mongoTemplate.findOne(
            query,
            Document.class,
            COLLECTION_NAME
        );

    return Optional.ofNullable(document)
        .map(this::toDomain);
  }

  private CheckIn toDomain(Document document) {

    Object checkedAtValue = document.get("checkedAt");

    Instant checkedAt;

    if (checkedAtValue instanceof java.util.Date date) {
      checkedAt = date.toInstant();
    } else if (checkedAtValue instanceof Instant instant) {
      checkedAt = instant;
    } else {
      throw new IllegalStateException(
          "Formato inválido para checkedAt"
      );
    }

    return new CheckIn(
        document.getObjectId("_id").toHexString(),
        document.getString("reservationCode"),
        document.getString("ticketCode"),
        document.getLong("customerId"),
        document.getLong("tripId"),
        document.getString("seatNumber"),
        document.getBoolean("checkedIn"),
        checkedAt
    );
  }
}
