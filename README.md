# Production Monitoring System

Sistema Full Stack para monitorear servidores en ambientes de manufactura, laboratorio y pruebas. El proyecto está pensado como una aplicación interna para equipos de Test Engineering, con trazabilidad por serial, control de estados, catálogo de pruebas, registro de fallas, acciones correctivas y reportes.

**Autor:** Juan Daniel López Pantoja

## Objetivo

Construir una base profesional para un sistema empresarial de monitoreo de producción, enfocado en servidores R9/R10, pruebas funcionales, fallas, evidencias y liberación controlada.

## Tecnologías

**Backend**
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security con JWT
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

**Herramientas**
- Docker Compose para MySQL
- Postman Collection en `docs/postman_collection.json`
- `.env.example`

## Funcionalidades

- Login y registro con JWT.
- Roles: `ADMIN`, `ENGINEER`, `TECHNICIAN`, `OPERATOR`.
- Gestión de servidores.
- Matriz de trazabilidad.
- Catálogo inicial de 16 pruebas.
- Registro de fallas con severidad y estado.
- Validaciones de negocio:
  - Serial obligatorio y único.
  - ID interno único.
  - No liberar servidores con fallas abiertas.
  - No liberar servidores con pruebas críticas fallidas.
- Dashboard con métricas principales.
- Filtros por texto en tablas.
- Reportes agregados y exportación CSV desde frontend.
- Seed data con usuarios, pruebas, servidores, PDUs, Raspberry devices y fallas.

## Usuarios iniciales

| Rol | Email | Password |
| --- | --- | --- |
| ADMIN | `admin@pms.local` | `admin123` |
| ENGINEER | `engineer@pms.local` | `engineer123` |
| TECHNICIAN | `technician@pms.local` | `tech123` |
| OPERATOR | `operator@pms.local` | `operator123` |

## Cómo correr MySQL

```bash
docker compose up -d
```

La base se crea como `production_monitoring` con usuario `root` y password `root`.

## Cómo correr backend

```bash
cd backend
mvn spring-boot:run
```

API:
- `http://localhost:8080/api`
- Swagger: `http://localhost:8080/swagger-ui.html`

Variables soportadas:

```env
DB_URL=jdbc:mysql://localhost:3306/production_monitoring?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=ProductionMonitoringSystemSecretKeyForJwtMustBeLongEnough2026
JWT_EXPIRATION_MS=86400000
```

## Cómo correr frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend:
- `http://localhost:5173`

Variable soportada:

```env
VITE_API_URL=http://localhost:8080/api
```

## Endpoints principales

| Método | Endpoint | Descripción |
| --- | --- | --- |
| POST | `/api/auth/login` | Login y generación de JWT |
| POST | `/api/auth/register` | Registro de usuario |
| GET | `/api/servers` | Listar servidores |
| POST | `/api/servers` | Crear servidor |
| GET | `/api/servers/{id}` | Consultar servidor |
| PUT | `/api/servers/{id}` | Actualizar servidor |
| DELETE | `/api/servers/{id}` | Eliminar servidor |
| GET | `/api/tests` | Consultar catálogo de pruebas |
| GET | `/api/server-tests` | Consultar resultados de pruebas |
| POST | `/api/server-tests` | Registrar resultado de prueba |
| GET | `/api/traceability` | Consultar matriz de trazabilidad |
| POST | `/api/traceability` | Crear registro de trazabilidad |
| GET | `/api/failures` | Listar fallas |
| POST | `/api/failures` | Registrar falla |
| GET | `/api/dashboard` | Métricas del dashboard |
| GET | `/api/reports/servers-by-status` | Reporte de servidores por estado |
| GET | `/api/reports/failures-by-test` | Reporte de fallas por prueba |
| GET | `/api/reports/failures-by-model` | Reporte de fallas por modelo |
| GET | `/api/pdus` | Listar PDUs |
| GET | `/api/raspberries` | Listar Raspberry devices |

## Estructura del repositorio

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

## Capturas sugeridas para GitHub

- Login.
- Dashboard con métricas.
- Lista de servidores filtrada.
- Detalle de servidor.
- Matriz de trazabilidad.
- Catálogo de pruebas.
- Reportes exportables.
- Swagger UI.

## Commits sugeridos

```bash
git add .
git commit -m "feat: create production monitoring backend"
git commit -m "feat: add react dashboard and monitoring views"
git commit -m "docs: add setup guide and postman collection"
```

## Próximas mejoras

- Edición completa de registros desde frontend.
- Pruebas unitarias y de integración.
- Exportación CSV desde backend.
- Auditoría por usuario autenticado.
- Carga de evidencias/logs en storage.
- Gráficas avanzadas por fecha, modelo y severidad.
- Pipeline CI/CD con GitHub Actions.
