# AGENTS.md - Drone Delivery Management System

## Project Overview
Spring Boot 3.2 application managing autonomous drone medication deliveries with stateful drone management, battery simulation, and REST API. Uses H2 in-memory database with scheduled state transitions.

## Architecture & Key Components

### Three-Tier Service Architecture
- **Controller Layer**: `DroneController` → single endpoint handler for all drone operations
- **Service Layer**: `DroneService` (business logic), `SchedulerService` (background automation)
- **Data Layer**: JPA repositories + H2 database with SQL initialization

### State Machine Pattern (Critical)
**Drones cycle through states automatically every 10 seconds:**
```
IDLE → LOADING → LOADED → DELIVERING → DELIVERED → RETURNING → IDLE
```
- State transitions happen in `SchedulerService.updateDroneStates()` running on fixed 10-second schedule
- Service validates drone state before allowing medication loads: only IDLE or LOADING drones can accept cargo
- State is used to filter available drones: `getAvailableDrones()` returns only IDLE drones

### Data Model (Three Entities)
- `Drone`: serial number (unique), model (enum), weight limit, battery %, state (enum)
- `Medication`: name, weight, code, image (blob)
- `DroneLoad`: mapping table linking drones to loaded medications (many-to-many)

**Critical Constraint**: Current load = sum of medication weights must not exceed drone.weightLimit

## Essential Patterns & Conventions

### Battery System
- Battery capacity ranges 0-100%
- Cannot load medications if battery < 25% (enforced in `DroneService.loadMedication()`)
- Battery drains 1-5% randomly every 10 seconds via scheduler
- Battery can hit 0; no minimum threshold for other operations

### Weight Calculation
Medication loading calculates cumulative weight:
```java
int currentLoad = droneLoadRepository.findByDroneId(droneId)
    .stream()
    .map(dl -> dl.getMedication().getWeight())
    .reduce(0, Integer::sum);
```
- Throws `OverweightException` if adding medication exceeds limit
- Sets drone state to LOADED when weight exactly equals limit, LOADING otherwise

### DTO Mapping Pattern
Manual mapping (no MapStruct). Three main DTOs:
- `DroneRegisterRequest` → `DroneResponse` (registration)
- `MedicationLoadRequest` (contains droneId, medicationId) → String response
- `DroneLoadResponse` (flattened view of drone + medication pairs)

### Exception Hierarchy
Custom exceptions in `exception/` package:
- `DroneNotFoundException` - missing drone ID
- `LowBatteryException` - battery < 25%
- `OverweightException` - weight limit exceeded
- `BadRequestException` - invalid states/inputs

## API Endpoints & Data Flows

### Medication Loading Flow (Stateful)
1. `POST /drones/load` accepts `MedicationLoadRequest`
2. `DroneService.loadMedication()` validates: drone exists, battery ≥ 25%, state is IDLE/LOADING
3. Calculates cumulative weight; throws if exceeds limit
4. Creates `DroneLoad` entity
5. Sets drone state to LOADED (if weight == limit) or LOADING (otherwise)
6. Returns "Medication loaded successfully"

**Key behavioral detail**: Multiple medications can be loaded incrementally until weight capacity is reached.

### Battery Constraint
Battery is checked BEFORE state transition. Medication cannot be loaded on drones with low battery even if state is IDLE.

## Build & Deployment

### Maven Commands
```bash
mvn clean install          # Build with tests
mvn spring-boot:run        # Start application (localhost:8080)
mvn test                   # Run unit tests only
```

### Database Initialization
- `schema.sql` creates tables on startup (drops if exist)
- `data.sql` seeds initial medications
- H2 console accessible at `http://localhost:8080/h2-console` (sa/password empty)
- DDL auto = `none` (manual control via SQL files)

### Swagger/OpenAPI
Available at `http://localhost:8080/swagger-ui/index.html` - auto-generated from controller annotations.

## Testing Patterns
Test files: `DroneControllerTest.java`, `DroneServiceTest.java`, `DronesApplicationTests.java`
- Use Spring `@WebMvcTest` / `@SpringBootTest` annotations
- Mock repositories in service tests
- Verify state transitions and weight calculations

## Dependencies Worth Knowing
- **Lombok**: `@Data`, `@RequiredArgsConstructor` - reduces boilerplate
- **SpringDoc OpenAPI**: Swagger UI auto-generation
- **Jakarta Validation**: `@Valid`, constraint annotations on DTOs and entities
- **Spring Scheduling**: `@Scheduled` annotation on `SchedulerService`

## File Structure Quick Reference
```
src/main/java/com/drone/delivery/drones/
├── controller/          # REST endpoints
├── service/            # DroneService, SchedulerService
├── entity/             # JPA entities (Drone, Medication, DroneLoad)
├── repository/         # Spring Data repositories
├── dto/               # Request/Response objects
├── enums/             # DroneState, DroneModel
└── exception/         # Custom exceptions

src/main/resources/
├── application.properties  # H2, JPA config (note: defer-datasource-initialization=true)
├── schema.sql             # Table creation
└── data.sql              # Initial medication seeding
```

## Common Modifications
- **Add API endpoint**: Create method in `DroneController`, implement in `DroneService`
- **Add validation**: Use Jakarta `@Constraint` or exception in service
- **Modify state machine**: Edit switch case in `SchedulerService.updateDroneStates()`
- **Change scheduler frequency**: Adjust `@Scheduled(fixedRate = 10000)` milliseconds in `SchedulerService`
- **Seed data**: Add INSERT statements to `data.sql`

## Critical Gotchas
1. **State persistence**: State changes are persisted immediately; SchedulerService runs in background
2. **Transaction boundaries**: `@Transactional` on `loadMedication()` only - other methods are not transactional
3. **Weight calculation is streamed**: Uses repository query + stream; no stored procedure
4. **Battery drain is random**: Each drone gets 1-5% random drain, not deterministic
5. **No scheduling configuration**: Uses Spring default task executor; no explicit thread pool config

