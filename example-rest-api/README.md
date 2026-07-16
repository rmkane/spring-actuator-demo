# Example REST API

A Spring Boot blog application demonstrating the shared `common-health` libraries, with Flyway-managed PostgreSQL persistence.

## Features

- **Spring Boot 3.5.14** on Java 21
- **PostgreSQL 17**, schema and seed data managed by **Flyway**
- **JPA/Hibernate** for data access
- **springdoc-openapi** for Swagger UI
- Actuator health checks composed from `common-health-datasource-postgres` plus an app-specific `DatabaseSchemaHealthIndicator`

## Database structure

Three entities, seeded via `src/main/resources/db/migration`:

- `User` — blog authors
- `Blog` — blog posts
- `Comment` — comments on blog posts

## Running

From the repository root (see the root [README](../README.md) for the Makefile-based workflow):

```bash
make run
```

This starts PostgreSQL via Docker Compose and runs the app on `http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health` (or `make health`)

`queries.sql` in the repo root has example read queries against the seeded data.
