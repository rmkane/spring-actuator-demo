# PostgreSQL Health Indicator

Provides an autoconfiguration for the `org.springframework.boot.actuate.health.HealthIndicator` interface, which is used by Spring Boot to determine whether a given application is "healthy" or not. The indicator verifies that a database connection exists and can be used successfully.
