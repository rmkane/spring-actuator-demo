# AGENTS.md

## Project overview

Multi-module Maven project: shared health-check libraries plus an example Spring Boot REST app using PostgreSQL and Flyway.

```text
.
├── pom.xml                                 # root aggregator only
├── common-pom/                             # aggregator for the two artifacts below
│   ├── common-starter-parent/              # actual Maven parent for every module
│   └── common-dependencies/                # shared dependency-management BOM
├── common-health/
│   ├── common-health-datasource-core/      # DB-neutral health-check abstraction
│   └── common-health-datasource-postgres/  # PostgreSQL-specific health indicator
├── example-rest-api/                       # runnable Spring Boot app
├── docker-compose.yml                      # local PostgreSQL for example-rest-api
├── scripts/health.sh                       # curls /actuator/health
├── Makefile                                # primary command interface
└── README.md
```

The root `pom.xml` is an aggregator only and contains the top-level `<modules>` list. `common-pom/pom.xml` is also an aggregator only.

Every buildable module's actual `<parent>` is `common-pom/common-starter-parent/pom.xml`. It extends `spring-boot-starter-parent`, explicitly enforces Java 21, imports `common-dependencies`, and provides shared plugin and resource configuration.

`common-dependencies` is a plain BOM containing shared dependency management for versions not already managed by Spring Boot, such as springdoc.

Do not put shared build configuration in either aggregator POM, and do not make an aggregator a module's `<parent>`.

## Module responsibilities

* **common-starter-parent** — Maven parent for every buildable module. Extends `spring-boot-starter-parent`, enforces Java 21, imports `common-dependencies`, and provides shared plugin and resource configuration. It is not a runtime dependency.
* **common-dependencies** — BOM containing dependency management only. Add shared dependency versions here when they are not already managed by Spring Boot or another imported BOM.
* **common-health** — Pure aggregator for the two datasource modules below. It contains no Java source.
* **common-health-datasource-core** — Contains `org.acme.common.health.core.AbstractDatasourceHealthIndicator`. Must remain database-neutral and must not depend on PostgreSQL-specific code.
* **common-health-datasource-postgres** — Contains `org.acme.common.health.postgres.PostgresHealthIndicator`. Depends on core; core must never depend back on this module.
* **example-rest-api** — Runnable application containing controllers, services, repositories, Flyway migrations, configuration, and application-specific health indicators such as `DatabaseSchemaHealthIndicator`. Depends on both shared health modules.

Dependency direction must remain one-way:

```text
common-health-datasource-core
            ↑
common-health-datasource-postgres
            ↑
      example-rest-api
```

Do not introduce reverse or circular dependencies.

## Java version

Java 21 is the required project version.

`common-starter-parent` must explicitly configure and enforce Java 21 for all child modules. Do not lower the Java version to Spring Boot's minimum supported version.

Do not override the Java version in individual child modules unless explicitly requested and justified.

Use Java 21 language features only when they improve the implementation and remain consistent with the surrounding code.

## Commands — use the Makefile

`make help` lists the available commands. Do not invent raw Maven or Docker commands when an existing Make target already covers the operation.

```text
make build          # mvn compile for the whole reactor
make install        # mvn clean install for the whole reactor
make clean          # mvn clean
make test           # mvn test for the whole reactor
make run            # docker-up, then run example-rest-api
make health         # scripts/health.sh — curls /actuator/health
make docker-up
make docker-down
make docker-logs
make docker-ps
```

There is no Maven Wrapper in this repository. The Makefile invokes plain `mvn`.

When the Makefile does not cover a necessary focused operation, use the narrowest appropriate Maven command and report it in the completion response. Examples include running one test class or validating one module.

Consider adding a reusable Make target when the same command is likely to be needed again.

## Editing workflow

Before changing Maven configuration:

1. Inspect the affected module's `pom.xml`.
2. Inspect `common-starter-parent/pom.xml`.
3. Inspect `common-dependencies/pom.xml` when dependencies or versions are involved.
4. Inspect the relevant aggregator POM when modules are being added, removed, or moved.
5. Confirm whether the change belongs in the module, shared parent, BOM, or aggregator.

Put configuration in the narrowest correct location:

* Module-specific dependencies and plugins belong in the affected module.
* Shared dependency versions belong in `common-dependencies`.
* Shared Java, plugin, resource, and build configuration belongs in `common-starter-parent`.
* Module declarations belong in the appropriate aggregator.
* Aggregators must not accumulate shared parent configuration.

Do not move application-specific code into shared modules.

Do not create a new shared abstraction unless it has a clear reusable purpose.

Do not make unrelated cleanup or formatting changes.

## Maven conventions

* Do not add explicit dependency versions to child POMs when the version is already managed by `spring-boot-dependencies`, `common-dependencies`, or another imported BOM.
* Add dependencies only to modules that directly need them.
* Do not add `common-starter-parent` or `common-dependencies` as runtime dependencies.
* Do not configure Spring Boot repackaging outside runnable application modules.
* Keep shared plugin defaults in `common-starter-parent`.
* Keep optional plugin execution module-specific unless every child module genuinely requires it.
* Do not use system-scoped dependencies.
* Do not add absolute local filesystem paths to POM files.
* When adding a module, add it to the appropriate aggregator and verify that its `<parent>` points to `common-starter-parent`.

## Java and Spring conventions

* Use constructor injection.
* Prefer immutable fields and values.
* Prefer guard clauses and early returns.
* Do not use wildcard imports.
* Keep controllers thin.
* Put business logic in services.
* Put persistence access in repositories.
* Keep configuration in dedicated configuration classes.
* Preserve the existing package structure and naming conventions.
* Do not introduce unnecessary interfaces or abstractions.
* Do not reformat unrelated code.
* Do not modify generated code or files under `target/`.

## Health indicators

Reusable database-neutral health-check behavior belongs in `common-health-datasource-core`.

Database-specific behavior belongs in the corresponding database module.

PostgreSQL-specific behavior belongs in `common-health-datasource-postgres`.

Application-specific schema or business health checks belong in `example-rest-api`.

Health checks should:

* Use lightweight read-only operations.
* Avoid modifying database state.
* Keep connectivity checks separate from application schema checks.
* Allow Spring Boot's health infrastructure to report failures.
* Avoid querying application tables unless the indicator explicitly checks application readiness.

## Flyway

Migrations live in:

```text
example-rest-api/src/main/resources/db/migration
```

Use sequential names in the form:

```text
V<N>__description.sql
```

Do not rewrite an already-applied migration. Add a new migration instead.

Keep migrations deterministic and update application code and tests when schema changes affect them.

Do not place application migrations in shared library modules.

## Docker

`docker-compose.yml` must remain consistent with:

```text
example-rest-api/src/main/resources/application.yml
```

Current local database settings are:

```text
database: blogdb
username: bloguser
port: 5432
```

Keep database name, username, password, port, and service hostname aligned between Docker and application configuration.

Do not commit non-development credentials.

## Validation workflow

Add or update tests when behavior changes.

After making changes:

1. Run the narrowest relevant tests.
2. Run `make test` when the change can affect multiple modules.
3. Run `make install` when the full packaged reactor or local artifact resolution must be verified.
4. Review the diff for unrelated changes.
5. Confirm that dependency direction remains valid.

For Maven parent, BOM, dependency-management, Java-version, or shared-library changes, prefer validating the complete reactor.

Do not remove, disable, or weaken tests merely to make the build pass.

Do not claim that a command passed unless it was actually executed successfully.

## Documentation

Update `README.md` when changes affect:

* Module structure.
* Build commands.
* Runtime prerequisites.
* Docker usage.
* Application configuration.
* Required environment variables.
* Shared-library usage.
* Public APIs.
* Developer setup.

Do not add comments that only restate the code.

## Repository safety

* Do not commit credentials, tokens, private keys, or production secrets.
* Do not edit build output under `target/`.
* Do not delete apparently unused code without searching all modules.
* Do not run destructive database commands.
* Do not perform Git commits, pushes, rebases, resets, or force operations unless explicitly requested.
* Do not modify unrelated files.

## Completion response

At completion, report:

* What changed.
* Which modules were affected.
* Whether Maven parent, BOM, or aggregator configuration changed.
* Which validation commands were run.
* Whether all builds and tests passed.
* Any commands that could not be run.
* Any unresolved assumptions or risks.
