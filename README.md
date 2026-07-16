# Spring Actuator Demo

A multi-module Maven project: shared Spring Boot Actuator health-check libraries, plus an example REST application that consumes them.

```text
.
├── common-pom/                             # aggregator: shared Maven parent + dependency BOM
│   ├── common-starter-parent/              # the actual <parent> for every module
│   └── common-dependencies/                # shared dependency-management BOM
├── common-health/                          # aggregator for shared health-check libraries
│   ├── common-health-datasource-core/      # DB-neutral health-check abstraction
│   └── common-health-datasource-postgres/  # PostgreSQL-specific health indicator
└── example-rest-api/                       # runnable Spring Boot blog app — see its own README
```

Each module has its own README with more detail.

## Prerequisites

- Java 21
- Maven
- Docker (for local PostgreSQL)

## Getting started

```bash
make help    # list all available commands
make run     # start PostgreSQL via Docker Compose, then run example-rest-api
make health  # curl the actuator health endpoint
```

See the [Makefile](Makefile) for the full command list (build, test, install, docker-up/down/logs/ps).

## Repository layout

- **common-pom** — see [common-pom](common-pom/) — the shared Maven parent/BOM split, not a runtime dependency.
- **common-health** — see [common-health](common-health/) — reusable, database-neutral and PostgreSQL-specific Actuator health indicators.
- **example-rest-api** — see [example-rest-api](example-rest-api/) — the runnable blog demo app.

`queries.sql` has example read queries against the app's seeded data; it isn't run automatically.
