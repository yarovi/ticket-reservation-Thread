# Estado actual del proyecto y observaciones de arquitectura hexagonal

Este documento resume las clases implementadas, las pendientes y los puntos que no están alineados con una arquitectura hexagonal limpia.

## 1. Clases implementadas

### 1.1 Dominio

| Paquete | Clase | Rol | Observación |
|---|---|---|---|
| `org.example.domain.model` | `ReservationRequest` | DTO de entrada para reservar un asiento | Está bien definido como `record`, pero no incluye validación de negocio ni reglas de dominio. |
| `org.example.domain.model` | `Ticket` | Entidad del asiento | Representa correctamente el estado del ticket. |
| `org.example.domain.model` | `Reservation` | Entidad de la reserva | Tiene datos básicos, pero no incorpora reglas como validación de duplicados ni cálculo de expiración. |
| `org.example.domain.model` | `Customer` | Modelo de cliente | Implementado pero no se usa en el caso de uso actual. |
| `org.example.domain.enums` | `TicketStatus` | Enum del estado del ticket | Tiene `AVAILABLE`, `RESERVED`, `SOLD`; coherente con el flujo principal. |
| `org.example.domain.enums` | `ScenarioType` | Enum de escenarios | Existe pero no está conectado a la lógica de negocio ni a un caso de uso real. |
| `org.example.domain.result` | `ReservationResult` | Contrato base de resultados | Bueno como sealed interface para representar resultados del caso de uso. |
| `org.example.domain.result` | `ReservationSuccess` | Resultado exitoso | Implementado. |
| `org.example.domain.result` | `ReservationRejected` | Resultado rechazado | Implementado. |
| `org.example.domain.result` | `ReservationTimeout` | Resultado de timeout | Implementado pero no se usa aún. |

### 1.2 Puertos de aplicación

| Paquete | Clase | Rol | Observación |
|---|---|---|---|
| `org.example.application.port.in` | `ReserveTicketUseCase` | Caso de uso de entrada | Define la operación central `reserve(...)`. |
| `org.example.application.port.in` | `MetricsPublisher` | Puerto de salida para métricas | Correcto para desacoplar la infraestructura de observabilidad. |
| `org.example.application.port.out` | `TicketRepository` | Puerto de persistencia de tickets | Bien definido para abstraer la base de datos. |
| `org.example.application.port.out` | `ReservationRepository` | Puerto de persistencia de reservas | Bien definido pero su firma depende de `Connection`, lo que mezcla más la infraestructura con la capa de aplicación. |

### 1.3 Casos de uso / servicios

| Paquete | Clase | Rol | Observación |
|---|---|---|---|
| `org.example.application.service` | `ReservationService` | Implementación del caso de uso | Es la pieza principal del flujo: valida si el asiento existe, si está disponible, crea la reserva y actualiza el ticket. |

### 1.4 Adaptadores / infraestructura

| Paquete | Clase | Rol | Observación |
|---|---|---|---|
| `org.example.boostrap` | `ApplicationConfiguration` | Composition root | Crea los repositorios reales y el servicio. Tiene sentido como punto de wiring. |
| `org.example.infraestructure.config` | `QueryProperties` | Adaptador de conexión JDBC | Cumple su propósito, pero introduce una dependencia concreta de infraestructura dentro del servicio. |
| `org.example.infraestructure.database` | `JdbcTicketRepository` | Adaptador JDBC para tickets | Implementa `findBySeat` y `update`. |
| `org.example.infraestructure.repository` | `JdbcReservationRepository` | Adaptador JDBC para reservas | Implementa `save(...)` y retorna la reserva creada. |
| `org.example.infraestructure.metric` | `ConsoleMetricsPublisher` | Adaptador de métricas | Es un adaptador simple y funcional. |
| `org.example.infraestructure` | `DatabaseConfiguration` | Configuración de H2 | Genera el `DataSource` en memoria. |
| `org.example.infraestructure.database` | `SchemaInitializer` | Inicializador de esquema SQL | Tiene la lógica de cargar `schema.sql` y `data.sql`. |
| `org.example.infraestructure.database` | `DataLoader` | Clase vacía | Está creada pero no está implementada ni conectada. |

### 1.5 Entrada principal de la aplicación

| Paquete | Clase | Rol | Observación |
|---|---|---|---|
| `org.example` | `App` | Punto de entrada de la app | Actualmente es un placeholder (`Hello World!`) y no inicia la aplicación ni usa `ReservationService`. |

## 2. Pendientes / trabajo faltante

1. Inicializar la aplicación real desde `App` o un bootstrap principal.
   - Hoy `App` no conecta la capa de aplicación con la infraestructura.
   - No hay flujo de arranque real ni ejecución de reserva.

2. Implementar adaptadores de entrada.
   - No hay REST controller, CLI, queue consumer ni ningún puerto de entrada real.
   - La aplicación aún no expone la funcionalidad al exterior.

3. Completar la semántica de dominio.
   - Faltan validaciones de negocio más ricas: cliente inexistente, asiento inválido, reserva duplicada, timeout, expiración, etc.
   - `ReservationTimeout` está definido pero no hay lógica que lo produzca.

4. Mejorar la gestión transaccional.
   - El servicio hace `ConnectionFactory.getConnection()` y maneja el commit/rollback directamente.
   - Esto es un acoplamiento fuerte a la infraestructura y dificulta la prueba unitaria del caso de uso.

5. Resolver la carga inicial de datos y recursos SQL.
   - `SchemaInitializer` intenta leer archivos SQL, pero estos no están en `src/main/resources` y el proyecto no parece estar usando ese flujo de forma consistente.

6. Completar o quitar clases huérfanas.
   - `DataLoader` está vacío y no se usa.
   - `ScenarioType` y `Customer` existen pero no están integrados en el caso de uso principal.

7. Añadir pruebas reales del flujo.
   - Solo hay una prueba mínima (`AppTest`) que no valida las reglas del caso de uso ni el comportamiento transaccional.

8. Definir un servicio de persistencia más limpio.
   - Los repositorios reciben `Connection` como parámetro, lo que mezcla más la abstracción del dominio con la capa técnica.
   - En una arquitectura hexagonal más limpia, el adaptador de infraestructura debería encapsular la transacción y el caso de uso debería depender solo de puertos.

## 3. Observaciones sobre arquitectura hexagonal

### Lo que sí está bien

- La capa de dominio está separada de la infraestructura.
- Los puertos (`application.port.in` y `application.port.out`) están presentes.
- El servicio de aplicación (`ReservationService`) orquesta la lógica de negocio y depende de interfaces, no de implementaciones concretas.
- Los adaptadores de infraestructura están aislados bajo `infraestructure`.

### Lo que no está alineado

1. Dependencia del servicio a infraestructura
   - `ReservationService` depende directamente de `QueryProperties`, que es una clase concreta del paquete `infraestructure`.
   - En hexagonal, el servicio de aplicación no debería conocer la tecnología de conexión ni el detalle de JDBC.
   - La transacción debería estar encapsulada en un adaptador o un caso de uso más técnico, no en el servicio de negocio.

2. Nomenclatura de paquetes
   - `boostrap` debería ser `bootstrap`.
   - `infraestructure` debería ser `infrastructure`.
   - Esta inconsistencia rompe la claridad del diseño y dificulta el mantenimiento.

3. Recursos SQL ubicados en un lugar incorrecto
   - `schema.sql` y `data.sql` están dentro de `src/main/java/.../database`.
   - En Maven/Java, los archivos SQL deben estar normalmente en `src/main/resources` para ser cargados con el `ClassLoader` de forma reproducible.
   - `SchemaInitializer` intenta cargarlos con `getResourceAsStream(fileName)`, lo cual puede fallar si no están en la ruta de recursos.

4. `JdbcReservationRepository` recibe `Connection` y además guarda un `connectionFactory` que no se usa.
   - Esto sugiere una implementación incompleta o una mezcla de responsabilidades.

5. El servicio mezcla lógica de negocio + manejo de base de datos
   - La validación del asiento y la actualización de la reserva están en el mismo servicio, pero la conexión y la transacción también están allí.
   - Esto hace que el caso de uso sea menos puro y más difícil de probar.

6. El `App` no es un bootstrap real
   - El punto de entrada no crea el contenedor ni invoca la lógica útil del dominio.
   - En una app hexagonal, el punto de entrada suele estar en la capa más externa y delegar al adapter o application layer, no quedarse en un `Hello World`.

## 4. Directorios / paquetes que no están alineados con la arquitectura

### Directorios con problemas claros

- `src/main/java/org/example/boostrap/`
  - Error ortográfico de `boostrap` → debe ser `bootstrap`.

- `infrastructure`
  - Error ortográfico de `infraestructure` → debe ser `infrastructure`.

- `infrastructure`
  - Mezcla varias responsabilidades: SQL schema, data load y repositorios JDBC.
  - La estructura debería separar mejor adaptadores de persistencia y scripts de base de datos.

- `infrastructure`
  - El repositorio de reservas está en `repository`, mientras `JdbcTicketRepository` está en `database`. Hay inconsistencia conceptual.

- `infrastructure`
  - El archivo SQL está en la ruta equivocada para recursos Java.

### Otras observaciones de diseño

- El paquete raíz `org.example` contiene el `App` base y no tiene una clara separación como módulo de arranque.
- La capa de aplicación debería tener un paquete dedicado a `adapter/in` y `adapter/out`, si se quiere respetar la convención hexagonal más estricta.
- Hoy el proyecto tiene la estructura conceptual, pero aún no la disciplina de diseño que la arquitectura exige.

## 5. Recomendación de próximos pasos

1. Renombrar los paquetes con errores de ortografía.
2. Mover `schema.sql` y `data.sql` a `src/main/resources`.
3. Crear un arreglo de inyección de dependencias más limpio (constructor injection con interfaces y configuración de wiring).
4. Separar transacciones y acceso a BD del servicio de caso de uso.
5. Implementar un adaptador de entrada (REST/CLI) y un bootstrapping real.
6. Completar el flujo de validación, timeout, manejo de errores y pruebas.
7. Eliminar o completar `DataLoader` y otros artefactos sin uso.

## 6. Conclusión

El proyecto ya tiene una base conceptual aceptable para una arquitectura hexagonal: dominio, puertos, servicio de aplicación y adaptadores. Sin embargo, aún hay varios puntos de acoplamiento técnico, errores de nomenclatura y recursos mal ubicados que impiden que la estructura quede completamente alineada con el patrón.

La principal mejora a realizar es reducir el acoplamiento del `ReservationService` con JDBC y reforzar la separación de capas para que la lógica de negocio quede más pura y testeable.
