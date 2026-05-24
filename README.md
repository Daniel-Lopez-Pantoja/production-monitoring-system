# Production Monitoring System

Enterprise-style Full Stack platform for manufacturing server validation, production monitoring, traceability, failure management, and Test Engineering workflows.

This project simulates an internal manufacturing system used to track R9/R10 server units through operating system installation, hardware validation, burn-in, debugging, retest, release readiness, and production handoff.

## Project Overview

Production Monitoring System is a portfolio-grade application designed around a real manufacturing and server validation domain. It provides a centralized platform for Test Engineering teams to manage server lifecycle status, execute validation tests, register failures, document corrective actions, maintain serial-level traceability, and monitor production readiness through an enterprise dashboard.

The project demonstrates backend engineering, REST API design, relational modeling, authentication, business rules, and a React-based operational UI in a domain that goes beyond a generic CRUD application.

## Business Context

Manufacturing and Test Engineering teams need reliable visibility into server units moving through validation. Without a centralized system, teams often rely on spreadsheets, manual updates, scattered logs, and inconsistent communication between engineers, technicians, quality teams, and operators.

This system addresses that problem by supporting:

- Server lifecycle tracking from intake to release.
- Validation test execution and result tracking.
- Failure registration with severity, status, evidence, and corrective actions.
- Serial-level traceability across server, rack, PDU, Raspberry device, test, technician, and engineer.
- Production readiness visibility through dashboards, metrics, and reports.

## Why This Project Matters

This project demonstrates software development applied to a realistic manufacturing and testing workflow. Instead of presenting a generic CRUD app, it models business rules, traceability requirements, validation states, operational metrics, and failure workflows found in enterprise production environments.

It is especially relevant for Java Backend, Full Stack, Enterprise Software, Manufacturing Systems, QA Automation, and Test Engineering support roles.

## Portfolio Highlights

- Java 17 Spring Boot REST API with layered architecture.
- JWT authentication and role-based access control.
- MySQL relational data model with JPA entities and repositories.
- React enterprise dashboard with dark mode support.
- Traceability matrix for serial-level validation history.
- Failure management module with severity, status, and corrective action tracking.
- Analytics and reporting views with CSV export support.
- Swagger/OpenAPI documentation for API exploration.
- Docker Compose setup for local MySQL development.
- Postman collection for API testing.

## Architecture

```text
React Frontend
      ↓
REST API
      ↓
Spring Boot Backend
      ↓
MySQL Database
```

The frontend consumes secured REST endpoints exposed by the Spring Boot backend. The backend handles authentication, business validation, persistence, seed data, reporting metrics, and operational workflows.

## Live Demo

- Live Demo: Coming soon
- API Demo: Coming soon

## Public Demo Safety

The public portfolio demo uses a restricted `DEMO_USER` account. This user can browse dashboards, reports, traceability, server data, failures, test catalog entries, and demo records, but cannot perform administrative or destructive actions.

Admin, engineer, technician, and operator credentials are not published for security reasons. The public demo account provides read-only/restricted access for portfolio review while protecting demo data from destructive changes.

Sensitive values such as production database credentials and JWT secrets must be configured through deployment provider environment variables, not committed to GitHub. The values in `.env.example` are placeholders for local development only.

## Technologies

**Backend**
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security with JWT
- MySQL
- Maven
- Swagger/OpenAPI

**Frontend**
- React
- JavaScript
- React Router
- Axios
- Recharts
- HTML/CSS
- Lucide React

**Tools**
- Docker Compose for MySQL
- Postman Collection located at `docs/postman_collection.json`
- `.env.example` for environment configuration

## Core Features

- JWT-based authentication and user registration.
- Role-based access model: `ADMIN`, `ENGINEER`, `TECHNICIAN`, `OPERATOR`.
- Server management for manufacturing and validation environments.
- Test catalog with manufacturing validation workflows.
- Server test execution tracking.
- Traceability matrix with rack, PDU, Raspberry, engineer, technician, status, result, and evidence fields.
- Failure management with severity, lifecycle status, corrective actions, comments, and log references.
- Dashboard with production overview, failure metrics, test metrics, charts, recent activity, and recent failures.
- Reports and analytics for server status, failures by test, failures by model, throughput, and release readiness.
- Search and filtering across operational modules.
- Business rule validation:
  - Required and unique server serial number.
  - Unique internal server ID.
  - Prevents server release when open failures exist.
  - Prevents server release when critical tests have failed.
- Seed data for users, roles, tests, servers, PDUs, Raspberry devices, test results, failures, and traceability records.

## Demo Credentials

| Role | Email | Password |
| --- | --- | --- |
| DEMO_USER | `demo@pms.local` | `demo123` |

This account is intended for portfolio review only and has restricted permissions to protect demo data.

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Daniel-Lopez-Pantoja/production-monitoring-system.git
cd production-monitoring-system
```

### 2. Start MySQL with Docker

```bash
docker compose up -d
```

The database is created as `production_monitoring` using `root` as the username and `root` as the password.

### 3. Run the Backend

```bash
cd backend
mvn spring-boot:run
```

Backend API:
- `http://localhost:8080/api`

Swagger/OpenAPI:
- `http://localhost:8080/swagger-ui.html`

Supported backend environment variables:

```env
DB_URL=jdbc:mysql://localhost:3306/production_monitoring?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=change-this-demo-jwt-secret-before-deployment-please
JWT_EXPIRATION_MS=86400000
```

### Existing Database Migration Note

If your local MySQL database was created before the `DEMO_USER` role was added, restart the backend after pulling the latest code. On startup, the application converts legacy role columns to `VARCHAR(50)` so new portfolio-safe roles can be inserted without MySQL enum truncation.

For a manual migration, run:

```sql
ALTER TABLE roles MODIFY COLUMN name VARCHAR(50) NOT NULL;
ALTER TABLE users MODIFY COLUMN role VARCHAR(50) NOT NULL;
```

### 4. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend:
- `http://localhost:5173`

Supported frontend environment variable:

```env
VITE_API_URL=http://localhost:8080/api
```

### 5. Log In

Use the restricted demo account for portfolio review:

```text
Email: demo@pms.local
Password: demo123
```

## API Documentation

Swagger UI is available after starting the backend:

```text
http://localhost:8080/swagger-ui.html
```

The Postman collection is available at:

```text
docs/postman_collection.json
```

## Main Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/auth/login` | Authenticates a user and returns a JWT |
| POST | `/api/auth/register` | Registers a new user using an ADMIN account |
| GET | `/api/servers` | Retrieves all registered servers |
| POST | `/api/servers` | Creates a new server record |
| GET | `/api/servers/{id}` | Retrieves a server by ID |
| PUT | `/api/servers/{id}` | Updates an existing server |
| DELETE | `/api/servers/{id}` | Deletes a server |
| GET | `/api/tests` | Retrieves the test catalog |
| GET | `/api/server-tests` | Retrieves server test execution results |
| POST | `/api/server-tests` | Registers a server test result |
| GET | `/api/traceability` | Retrieves the traceability matrix |
| POST | `/api/traceability` | Creates a traceability record |
| GET | `/api/failures` | Retrieves registered failures |
| POST | `/api/failures` | Registers a new failure |
| GET | `/api/dashboard` | Retrieves dashboard metrics |
| GET | `/api/reports/servers-by-status` | Retrieves server counts grouped by status |
| GET | `/api/reports/failures-by-test` | Retrieves failure counts grouped by test |
| GET | `/api/reports/failures-by-model` | Retrieves failure counts grouped by server model |
| GET | `/api/pdus` | Retrieves registered PDUs |
| GET | `/api/raspberries` | Retrieves registered Raspberry devices |

## Repository Structure

```text
Production Monitoring System/
├── backend/
│   ├── src/main/java/com/production/monitoring/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── model/entity/
│   │   ├── model/enums/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   └── pom.xml
├── frontend/
│   ├── src/api/
│   ├── src/components/
│   ├── src/context/
│   ├── src/pages/
│   └── package.json
├── docs/
│   └── postman_collection.json
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Application Preview

### Login Page
![Login](docs/images/login-page.png)

### Dashboard
![Dashboard](docs/images/dashboard-overview.png)

### Server Management
![Server Management](docs/images/server-management.png)

### Traceability Matrix
![Traceability Matrix](docs/images/traceability-matrix.png)

### Failure Management
![Failure Management](docs/images/failure-management.png)

### Test Catalog
![Test Catalog](docs/images/test-catalog.png)

### Reports & Analytics
![Reports](docs/images/reports-dashboard.png)

### Swagger API
![Swagger](docs/images/swagger-api.png)

## Future Improvements

- Complete create/edit workflows for all operational frontend modules.
- Unit, integration, and API test coverage for services, controllers, and business rules.
- Backend-driven CSV and PDF report generation.
- Audit trail by authenticated user for traceability and failure history.
- Evidence and log file upload using external or cloud storage.
- Advanced analytics by date range, server model, severity, test category, and production line.
- CI/CD pipeline with GitHub Actions.
- Deployment profile for cloud or containerized environments.
- Role-specific dashboards for engineers, technicians, operators, and administrators.

## Author

Juan Daniel López Pantoja

GitHub: [Daniel-Lopez-Pantoja](https://github.com/Daniel-Lopez-Pantoja)
