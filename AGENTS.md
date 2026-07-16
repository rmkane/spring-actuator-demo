# AGENTS.md

## Project overview

This repository is a multi-module Maven project containing shared libraries and an example Spring Boot REST application.

The Maven structure intentionally separates aggregation from inheritance:

* The root `pom.xml` is the top-level module aggregator.
* `common-pom/pom.xml` is the shared parent POM.
* Child modules inherit shared dependency, plugin, Java, and build configuration from `common-pom`.
* The root aggregator POM must not be treated as the shared parent unless the project structure is explicitly changed.

Before modifying Maven configuration, inspect both:

1. The root aggregator `pom.xml`.
2. `common-pom/pom.xml`.
3. The affected module's `pom.xml`.

## Repository structure

```text
.
├── pom.xml
├── common-pom/
│   └── pom.xml
├── common-health/
│   ├── pom.xml
│   ├── common-health-datasource-core/
│   │   ├── pom.xml
│   │   └── src/
│   └── common-health-datasource-postgres/
│       ├── pom.xml
│       └── src/
├── example-rest-api/
│   ├── pom.xml
│   └── src/
├── docker-compose.yml
├── queries.sql
├── run.sh
└── README.md
```

## Module responsibilities

### Root aggregator

The root `pom.xml` exists to aggregate modules for reactor builds.

Its responsibilities include:

* Declaring modules in `<modules>`.
* Providing a single entry point for repository-wide Maven builds.
* Controlling reactor build order through module relationships.

Do not place shared dependency management, plugin management, or Java configuration in the root aggregator merely because it is the root POM.

### `common-pom`

`common-pom` is the shared Maven parent.

Its responsibilities include:

* Java version configuration.
* Dependency management.
* Plugin management.
* Shared build configuration.
* Shared Maven properties.
* Imported BOMs.
* Organization-wide defaults used by child modules.

When adding or updating a version shared by multiple modules, prefer changing `common-pom/pom.xml`.

Do not add `common-pom` as a runtime dependency. It is a Maven parent POM, not a Java library.

### `common-health`

`common-health/pom.xml` is an aggregator for health-related shared libraries.

It contains:

* `common-health-datasource-core`
* `common-health-datasource-postgres`

Do not place Java source code directly in the `common-health` aggregator unless the module is intentionally converted into a library module.

### `common-health-datasource-core`

This module contains database-independent datasource health-check infrastructure.

Current primary type:

```text
org.acme.common.health.core.AbstractDatasourceHealthIndicator
```

Code in this module should remain database-neutral.

Do not introduce PostgreSQL-specific SQL, drivers, APIs, or behavior into this module.

Database-specific implementations should extend or use the abstractions defined here.

### `common-health-datasource-postgres`

This module contains PostgreSQL-specific datasource health-check functionality.

Current primary type:

```text
org.acme.common.health.postgres.PostgresHealthIndicator
```

This module may depend on `common-health-datasource-core`.

The core module must not depend on this PostgreSQL module.

Avoid introducing application-specific behavior into this shared library.

### `example-rest-api`

This is the runnable Spring Boot example application.

Its responsibilities include:

* Demonstrating use of the shared modules.
* REST controllers and API behavior.
* Application services and repositories.
* Persistence configuration.
* Flyway migrations.
* Application-specific health indicators.
* Runtime configuration.

Application-specific code belongs here and should not be moved into a shared module unless it is genuinely reusable.

## Dependency direction

Maintain the following dependency direction:

```text
common-health-datasource-core
            ↑
common-health-datasource-postgres
            ↑
      example-rest-api
```

The application may depend on shared modules.

Database-specific shared modules may depend on database-neutral core modules.

Core shared modules must not depend on database-specific modules or applications.

Do not introduce circular dependencies.

## Maven parent rules

Child modules should inherit from `common-pom`.

When inspecting or creating a child module, verify that its `<parent>` points to the shared parent coordinates and uses the correct relative path.

For a direct child of the repository root, the relative path will commonly resemble:

```xml
<relativePath>../common-pom/pom.xml</relativePath>
```

For a module nested under `common-health`, the relative path will commonly resemble:

```xml
<relativePath>../../common-pom/pom.xml</relativePath>
```

Do not change a parent relative path without checking the module's filesystem depth.

Do not make the root aggregator the parent merely to simplify a relative path.

## Maven aggregation rules

When adding a new module:

1. Add the module to the appropriate aggregator's `<modules>` section.
2. Make the module inherit from `common-pom`.
3. Add it to the root aggregator only when it is not already reached through a nested aggregator, or when the repository deliberately lists all modules at the root.
4. Verify the Maven reactor does not include the same module twice.
5. Verify dependency order through Maven dependencies rather than relying only on module declaration order.

Health-related libraries should generally be added beneath `common-health`.

Runnable examples or applications should generally be added at the repository root alongside `example-rest-api`.

## Dependency management

Before adding a dependency:

1. Check whether its version is already managed by `common-pom`.
2. Check whether it is managed by an imported BOM.
3. Check whether the dependency is already available transitively.
4. Add the dependency only to modules that directly use it.

Do not add explicit dependency versions to child modules when those versions are managed by `common-pom` or an imported BOM.

Do not add dependencies to the root aggregator unless the root is intentionally changed into a buildable Java module.

Shared libraries should expose the smallest reasonable dependency surface.

Use `optional`, runtime, provided, or test scopes where appropriate rather than exposing every dependency transitively.

## Plugin management

Shared plugin versions and default configuration belong in:

```text
common-pom/pom.xml
```

Use `<pluginManagement>` for plugins that should be available with shared versions and defaults but should only execute when declared by a child module.

Use `<build><plugins>` in `common-pom` only for plugins that must execute for every inheriting module.

Application-specific plugin execution belongs in the application module.

Do not configure Spring Boot repackaging for shared library modules.

The runnable `example-rest-api` module may use the Spring Boot Maven plugin to produce an executable artifact.

## Build commands

Run Maven commands from the repository root unless a module-specific command is needed.

Use the Maven Wrapper when the repository contains one. Otherwise, use `mvn`.

Build the entire reactor:

```bash
mvn clean verify
```

Build without running tests:

```bash
mvn clean package -DskipTests
```

Build all health modules:

```bash
mvn -pl common-health -am clean verify
```

Build the datasource core module:

```bash
mvn -pl common-health/common-health-datasource-core -am clean verify
```

Build the PostgreSQL health module and its dependencies:

```bash
mvn -pl common-health/common-health-datasource-postgres -am clean verify
```

Build the example application and required shared modules:

```bash
mvn -pl example-rest-api -am clean verify
```

Run tests for the example application:

```bash
mvn -pl example-rest-api -am test
```

Run one test class in the example application:

```bash
mvn -pl example-rest-api -am -Dtest=<TestClassName> test
```

Run the Spring Boot application only after required shared artifacts have been built:

```bash
mvn -pl example-rest-api -am spring-boot:run
```

Do not run Maven from a child module when a reactor build is needed to resolve sibling modules that have not been installed locally.

Prefer `-pl <module> -am` for focused builds.

## Java conventions

* Use the Java version configured by `common-pom`.
* Prefer constructor injection.
* Prefer immutable fields.
* Use `final` where appropriate.
* Prefer guard clauses and early returns.
* Keep classes focused on one responsibility.
* Avoid unnecessary interfaces and abstractions.
* Do not introduce database-specific behavior into database-neutral modules.
* Preserve existing package structure.
* Do not use wildcard imports.
* Do not reformat unrelated code.
* Do not modify generated files or anything under `target/`.

Import order:

1. Static imports.
2. `java.*`
3. `jakarta.*`
4. Third-party imports such as `org.*` and `com.*`
5. Project-local imports.

## Spring Boot conventions

These rules primarily apply to `example-rest-api`.

* Prefer constructor injection over field injection.
* Keep controllers focused on HTTP concerns.
* Put business behavior in services.
* Put persistence access in repositories.
* Keep configuration in dedicated configuration classes.
* Use typed configuration properties for groups of application settings.
* Do not access the Spring application context directly unless necessary.
* Preserve actuator health semantics.
* Do not expose sensitive health details without considering actuator configuration.
* Keep application-specific health indicators in the application module.
* Keep reusable datasource health infrastructure in the shared health modules.

## Datasource health conventions

`AbstractDatasourceHealthIndicator` should provide reusable, database-neutral behavior.

A database-specific implementation should supply database-specific defaults, such as a validation query.

For PostgreSQL, the validation query should normally be:

```sql
SELECT 1
```

Health checks should:

* Execute a lightweight validation query.
* Report `UP` only when the query completes successfully.
* Allow Spring Boot's health indicator infrastructure to report failures.
* Avoid modifying database state.
* Avoid querying application tables unless the health indicator explicitly checks application schema readiness.
* Keep connection-level health separate from schema- or application-level health where practical.

Application health indicators such as `DatabaseSchemaHealthIndicator` may validate application-specific schema requirements, but that behavior must remain in `example-rest-api`.

## Database and Flyway conventions

Flyway migrations are located under:

```text
example-rest-api/src/main/resources/db/migration
```

When changing the database schema:

* Add a new migration.
* Do not rewrite an already-applied migration unless explicitly working in a disposable development environment and instructed to do so.
* Use sequential Flyway version names.
* Keep migrations deterministic.
* Avoid destructive changes unless explicitly requested.
* Update application code and tests to match schema changes.
* Do not put application migrations into shared health modules.

## Testing requirements

Behavior changes should include appropriate tests.

For shared health modules:

* Test the shared abstraction independently where practical.
* Test database-specific defaults in the database-specific module.
* Avoid requiring a live database for tests that can use mocks or stubs.
* Use integration tests when actual JDBC or PostgreSQL behavior is important.

For `example-rest-api`:

* Add unit tests for isolated business behavior.
* Add repository or integration tests for persistence behavior.
* Add web tests for controller behavior where appropriate.
* Add application-context tests for wiring changes.
* Validate Flyway and PostgreSQL integration when database configuration changes.

Do not remove, disable, or weaken tests merely to make the build pass.

## Editing workflow

Before editing:

1. Inspect the root aggregator POM.
2. Inspect `common-pom/pom.xml`.
3. Inspect the affected module's POM.
4. Identify upstream and downstream module dependencies.
5. Search for existing project conventions and similar implementations.
6. Determine whether the change is shared or application-specific.

While editing:

* Make the smallest coherent change that satisfies the request.
* Keep shared code independent of application code.
* Preserve public APIs unless a change is explicitly required.
* Update all affected call sites when changing an API.
* Avoid moving code between modules without a clear dependency reason.
* Do not create a new shared abstraction for a single use case without a clear extension need.
* Do not make unrelated cleanup changes.

## Validation workflow

After changing a shared module:

1. Build and test that module.
2. Build modules that depend on it.
3. Build `example-rest-api` when it consumes the changed module.
4. Run the complete reactor build when practical.

For a change to `common-health-datasource-core`, validate at least:

```bash
mvn -pl common-health/common-health-datasource-core -am test
mvn -pl common-health/common-health-datasource-postgres -am test
mvn -pl example-rest-api -am test
```

For a change to `common-health-datasource-postgres`, validate at least:

```bash
mvn -pl common-health/common-health-datasource-postgres -am test
mvn -pl example-rest-api -am test
```

For an application-only change, validate at least:

```bash
mvn -pl example-rest-api -am test
```

Before considering a repository-wide change complete, prefer:

```bash
mvn clean verify
```

Do not claim a command passed unless it was actually executed successfully.

## Docker and runtime files

`docker-compose.yml` provides local infrastructure for the example application.

Before changing it:

* Inspect `example-rest-api/src/main/resources/application.yml`.
* Preserve matching database names, ports, usernames, and service hostnames.
* Do not commit credentials intended for non-development environments.
* Keep local-development defaults clearly separated from production configuration.

Treat `queries.sql` as a developer aid unless its documented role says otherwise. Do not assume it is executed automatically by the application.

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
* Do not use system-scoped Maven dependencies.
* Do not add absolute local filesystem paths to POM files.
* Do not delete apparently unused code without searching all modules.
* Do not run destructive database commands.
* Do not perform Git commits, pushes, rebases, resets, or force operations unless explicitly requested.
* Do not modify unrelated files.

## Completion response

At completion, report:

* What changed.
* Which modules were affected.
* Whether Maven parent or aggregator configuration changed.
* Which validation commands were run.
* Whether all builds and tests passed.
* Any commands that could not be run.
* Any unresolved assumptions or risks.
