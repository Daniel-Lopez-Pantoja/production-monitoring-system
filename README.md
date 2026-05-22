# Production Monitoring System

Full Stack enterprise-oriented application for monitoring servers across manufacturing, laboratory, and test engineering environments. The system is designed as an internal production tracking platform for Test Engineering teams, providing serial-level traceability, lifecycle state management, test execution tracking, failure registration, corrective actions, evidence references, and operational reporting.

**Author:** Juan Daniel López Pantoja

## Objective

Build a professional foundation for an enterprise production monitoring system focused on R9/R10 server validation, functional testing workflows, failure management, technical evidence, corrective actions, and controlled server release processes.

This project is intended to demonstrate Java Backend Development skills in a real-world business domain, including layered architecture, REST API design, JWT-based security, relational data modeling, validation rules, and a React-based operational dashboard.

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
- HTML/CSS
- Lucide React

**Tools**
- Docker Compose for MySQL
- Postman Collection located at `docs/postman_collection.json`
- `.env.example` for environment configuration

## Features

- Authentication and user registration using JWT.
- Role-based access model: `ADMIN`, `ENGINEER`, `TECHNICIAN`, `OPERATOR`.
- Server asset management for production and test environments.
- Traceability matrix for complete server and test history.
- Initial test catalog with 16 manufacturing and validation test cases.
- Failure tracking with severity, status, corrective action, comments, and evidence references.
- Business rule validation:
  - Required and unique server serial number.
  - Unique internal server ID.
  - Prevents server release when open failures exist.
  - Prevents server release when critical tests have failed.
- Dashboard with key production and test metrics.
- Text-based filtering across operational tables.
- Aggregated reports with CSV export from the frontend.
- Seed data for users, roles, tests, servers, PDUs, Raspberry devices, server test results, and failures.

## Initial Users

| Role | Email | Password |
| --- | --- | --- |
| ADMIN | `admin@pms.local` | `admin123` |
| ENGINEER | `engineer@pms.local` | `engineer123` |
| TECHNICIAN | `technician@pms.local` | `tech123` |
| OPERATOR | `operator@pms.local` | `operator123` |

## Running MySQL

```bash
docker compose up -d
```

The database is created as `production_monitoring` using `root` as the username and `root` as the password.

## Running the Backend

```bash
cd backend
mvn spring-boot:run
```

API:
- `http://localhost:8080/api`
- Swagger: `http://localhost:8080/swagger-ui.html`

Supported environment variables:

```env
DB_URL=jdbc:mysql://localhost:3306/production_monitoring?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=ProductionMonitoringSystemSecretKeyForJwtMustBeLongEnough2026
JWT_EXPIRATION_MS=86400000
```

## Running the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend:
- `http://localhost:5173`

Supported environment variable:

```env
VITE_API_URL=http://localhost:8080/api
```

## Main Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/auth/login` | Authenticates a user and returns a JWT |
| POST | `/api/auth/register` | Registers a new user |
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

## Suggested GitHub Screenshots

- Login screen.
- Dashboard with operational metrics.
- Filtered server inventory view.
- Server detail view.
- Traceability matrix.
- Test catalog.
- Exportable reports.
- Swagger UI documentation.

## Suggested Commits

```bash
git add .
git commit -m "feat: create production monitoring backend"
git commit -m "feat: add react dashboard and monitoring views"
git commit -m "docs: add setup guide and postman collection"
```

## Future Improvements

- Full create/edit workflows for all operational records from the frontend.
- Unit and integration test coverage for services, controllers, and business rules.
- Backend-driven CSV export for reports.
- Authenticated user auditing for traceability and failure history.
- Evidence and log file upload using external or cloud storage.
- Advanced analytics by date range, server model, failure severity, and test category.
- CI/CD pipeline with GitHub Actions.
