# Drone Delivery Management System
A Spring Boot application for managing drone-based medication deliveries.
## Technology Stack
- Java 21
- Spring Boot 3.x
- Spring Data JPA
- H2 in-memory database
- Maven
## Features
- Register drones  
- Load drones with medications  
- Check loaded medications  
- Check drone availability  
- Check drone battery level  
- Automated state transitions via scheduler  
- REST API (JSON)  
## Setup & Installation
### Prerequisites
- Java 21+  
- Maven 3.8+  
- IntelliJ IDEA (or any Java IDE)
### Steps to Run
1. Clone the repository: `git clone https://github.com/your-username/drone-delivery.git`
2. Open in IntelliJ IDEA
3. Run `DroneDeliveryApplication.java`
4. Access APIs via Postman or Swagger
   -http://localhost:8080/swagger-ui/index.html#/
## API Documentation
### REST Endpoints
#### 1. Register Drone
- **POST /drones/register**  
  Request Body: `DroneRegisterRequest`  
  Response: `DroneResponse`
#### 2. Load Medication
- **POST /drones/load**  
  Request Body: `MedicationLoadRequest`  
  Response: Success message
#### 3. Get Loaded Medications
- **GET /drones/{droneId}/medications**  
  Response: List of `DroneLoadResponse`
#### 4. Get Available Drones
- **GET /drones/available**  
  Response: List of `DroneResponse`
#### 5. Check Battery Level
- **GET /drones/{droneId}/battery**  
  Response: Battery capacity (int)
## Database Schema
### Entities
- `Drone`
- `Medication`
- `DroneLoad` (mapping entity)
## H2 In-Memory Database
- Uses `schema.sql` for schema creation  
- Uses `data.sql` for seeding initial data  
## Scheduler
Runs every **10 seconds** to:
- Drain battery (1–5% random reduction)
- Transition drone states:
  - LOADED → DELIVERING  
  - DELIVERING → DELIVERED  
  - DELIVERED → RETURNING  
  - RETURNING → IDLE  
## Configuration
### application.properties
