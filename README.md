Public Grievance Management System

A robust, backend API built with Spring Boot designed to handle the submission, tracking, and resolution of public grievances and complaints. This system features an automated Service Level Agreement (SLA) engine that escalates overdue grievances, role-based access control, and a comprehensive RESTful API.

Features

- **Grievance Submission:** Citizens can seamlessly submit new grievances categorized by specific domains (e.g., Infrastructure, Sanitation).
- **Status Tracking & Updates:** Department officials can update the status of grievances (Open, In Progress, Resolved).
- **SLA Escalation Engine:** A background job automatically monitors open grievances. If a grievance breaches its assigned category's Service Level Agreement (SLA), it is automatically escalated to higher authorities, and an Escalation record is generated.
- **Event-Driven Notifications:** Employs Spring ApplicationEvents to trigger system-wide alerts and notifications the moment a grievance breaches SLA.
- **Citizen Feedback & Ratings:** Once a grievance is resolved, citizens can submit a satisfaction rating.
- **Pagination & Sorting:** Highly optimized endpoints that return paginated data for efficient dashboard rendering.
- **Security:** Built-in Spring Security integration.

## Tech Stack

- **Framework:** Spring Boot 3.3.4 (Java 21)
- **Database:** MySQL (via Spring Data JPA & Hibernate)
- **Build Tool:** Maven
- **Utilities:** Lombok, SLF4J (Logging)
- **API Documentation:** Swagger/OpenAPI

## Setup and Installation

### Prerequisites
- JDK 21 or higher installed
- MySQL Server installed and running
- Maven installed (optional, wrapper included)

### Database Configuration
1. Open MySQL and create a database named `java_project` (or update `application.properties` to match your DB name).
2. Update the credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/java_project
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   spring.jpa.hibernate.ddl-auto=update
   ```

### Running the Application

**Using Maven:**
```bash
mvn clean install
mvn spring-boot:run
```

**Using an IDE (IntelliJ / Eclipse / VS Code):**
- Import the project as a Maven project.
- Run `JavaProjectApplication.java` from your IDE.

## API Endpoints

The API base path is `http://localhost:8080/api/grievances`.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Submit a new grievance (Requires JSON body) |
| `PUT` | `/{id}/status` | Update the status of a grievance |
| `POST` | `/{id}/rate` | Submit a rating for a resolved grievance |
| `GET` | `/department/{id}` | Fetch paginated grievances for a specific department |
| `GET` | `/` | Fetch all grievances (supports pagination: `?page=0&size=10`) |
| `POST` | `/test/trigger-sla` | Manually trigger the SLA escalation engine (for testing) |

*Note: Swagger documentation is available by navigating to `http://localhost:8080/swagger-ui.html` while the app is running.*

## Architecture & Design Patterns

- **Layered Architecture:** Clear separation between Controllers (API Layer), Services (Business Logic), and Repositories (Data Access).
- **Observer Pattern:** Uses Spring's Event Listener mechanism to decoupled notification logic from core business logic when an SLA is breached.
- **DTOs:** Data Transfer Objects are used to separate API request bodies from internal database entities.
