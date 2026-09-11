src/main/java/org/example/ticketreservation
│
├── TicketReservationApplication.java
│
├── domain
│   │
│   ├── model
│   │   ├── Customer.java
│   │   ├── Transport.java
│   │   ├── Trip.java
│   │   ├── Seat.java
│   │   ├── Reservation.java
│   │   ├── Payment.java
│   │   ├── Ticket.java
│   │   └── CheckIn.java
│   │
│   ├── enums
│   │   ├── SeatStatus.java
│   │   ├── ReservationStatus.java
│   │   ├── PaymentStatus.java
│   │   └── PaymentMethod.java
│   │
│   ├── event
│   │   └── PassengerCheckedInEvent.java
│   │
│   └── exception
│       ├── CustomerNotFoundException.java
│       ├── TripNotFoundException.java
│       ├── SeatNotFoundException.java
│       ├── SeatNotAvailableException.java
│       ├── InvalidSeatForTripException.java
│       ├── ReservationNotFoundException.java
│       ├── ReservationExpiredException.java
│       ├── ReservationCannotBeCancelledException.java
│       ├── InvalidPaymentCodeException.java
│       ├── ReservationNotConfirmedException.java
│       ├── TicketNotFoundException.java
│       └── PassengerAlreadyCheckedInException.java
│
├── application
│   │
│   ├── command
│   │   ├── CreateCustomerCommand.java
│   │   ├── CreateReservationCommand.java
│   │   ├── ConfirmReservationCommand.java
│   │   └── CheckInCommand.java
│   │
│   ├── port
│   │   │
│   │   ├── in
│   │   │   ├── CreateCustomerUseCase.java
│   │   │   ├── ReserveTicketUseCase.java
│   │   │   ├── CancelReservationUseCase.java
│   │   │   ├── ConfirmReservationUseCase.java
│   │   │   ├── SearchReservationUseCase.java
│   │   │   ├── ListSeatsUseCase.java
│   │   │   └── CheckInUseCase.java
│   │   │
│   │   └── out
│   │       ├── CustomerRepository.java
│   │       ├── TripRepository.java
│   │       ├── SeatRepository.java
│   │       ├── ReservationRepository.java
│   │       ├── PaymentRepository.java
│   │       ├── TicketRepository.java
│   │       ├── CheckInRepository.java
│   │       ├── CheckInEventPublisher.java
│   │       ├── MetricsPublisher.java
│   │       └── CodeGenerator.java
│   │
│   └── service
│       ├── CustomerService.java
│       ├── ReservationService.java
│       ├── ConfirmationService.java
│       ├── CancelReservationService.java
│       ├── SearchReservationService.java
│       ├── ListSeatsService.java
│       └── CheckInService.java
│
├── adapter
│   │
│   ├── in
│   │   │
│   │   ├── rest                 <-- todavía no implementado
│   │   │   ├── controller
│   │   │   ├── request
│   │   │   ├── response
│   │   │   └── error
│   │   │
│   │   └── grpc                 <-- lo haremos más adelante
│   │
│   └── out
│       │
│       ├── persistence
│       │   │
│       │   ├── jdbc
│       │   │   ├── JdbcCustomerRepository.java
│       │   │   ├── JdbcTripRepository.java
│       │   │   ├── JdbcSeatRepository.java
│       │   │   ├── JdbcReservationRepository.java
│       │   │   ├── JdbcPaymentRepository.java
│       │   │   └── JdbcTicketRepository.java
│       │   │
│       │   └── mongo            <-- después
│       │       └── MongoCheckInRepository.java
│       │
│       ├── messaging
│       │   └── pubsub           <-- después
│       │       └── GooglePubSubCheckInEventPublisher.java
│       │
│       ├── metrics
│       │   └── MicrometerMetricsPublisher.java
│       │
│       └── generator
│           └── RandomCodeGenerator.java
│
└── infrastructure
└── config
├── ApplicationConfiguration.java
├── ReservationProperties.java
├── MongoConfiguration.java       <-- después, si se necesita
└── PubSubConfiguration.java      <-- después, si se necesita