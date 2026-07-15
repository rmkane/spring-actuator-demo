package org.acme.demo.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "management.health.db.enabled", havingValue = "true", matchIfMissing = true)
public class PostgresHealthIndicator implements HealthIndicator {

    private static final String DATABASE_NAME = "PostgreSQL";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            // Perform a simple SELECT 1 query to verify database is functional
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            return Health.up()
                .withDetail("database", DATABASE_NAME)
                .withDetail("status", "healthy")
                .withDetail("message", "Database connection and query successful")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", DATABASE_NAME)
                .withDetail("status", "unhealthy")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
