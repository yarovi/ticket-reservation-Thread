
Entregables
Proyecto Maven Java 21.
Arquitectura hexagonal creada.
H2 embebido.
Repositorio JDBC.
ExecutorService con Platform Threads.
Logs estructurados.
Docker con Grafana + Loki + Promtail.
Dashboard básico con:
Reservas exitosas.
Reservas fallidas.
Tiempo promedio.
Threads activos.


---------------------------- 
#Flujo de reserva de tickets
Cliente

      │

      ▼

ReservationRequest

      │

      ▼

ReserveTicketUseCase

      │

      ▼

ReservationService

      │
Telemetria
      ▼

TicketRepository

      │

¿Existe el asiento?

      │

NO --------------------------► ReservationRejected

SI

      │

¿Está disponible?

      │

NO --------------------------► ReservationRejected

SI

      │

Crear Reservation

      │

ReservationRepository.save()

      │

MetricsPublisher.publish()

      │

ReservationSuccess

-------------------
#Flujo general
java21-concurrency-lab

                           Controller
                               │
                               ▼
                    Scenario Orchestrator
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
          ▼                    ▼                    ▼
     Application          Observability        Scenario Runner
          │                    │                    │
          ▼                    ▼                    ▼
                     Domain Services
                            │
          ┌─────────────────┴────────────────┐
          ▼                                  ▼
Reservation Port                  Metrics Port
│                                  │
▼                                  ▼
Infrastructure                   Grafana/Loki
│
▼
H2
