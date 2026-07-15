package org.acme.demo.health;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "management.health.schema.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSchemaHealthIndicator implements HealthIndicator {

    private static final String DATABASE_NAME = "PostgreSQL";
    private static final String[] REQUIRED_TABLES = {"blogs", "comments", "users"};

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            // Check if our main tables exist
            for (String table : REQUIRED_TABLES) {
                String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, table);
                if (count == 0) {
                    return Health.down()
                        .withDetail("database", DATABASE_NAME)
                        .withDetail("status", "unhealthy")
                        .withDetail("error", "Table " + table + " not found")
                        .build();
                }
            }

            // Verify we can query the tables
            for (String table : REQUIRED_TABLES) {
                String sql = "SELECT COUNT(*) FROM " + table;
                jdbcTemplate.queryForObject(sql, Integer.class);
            }

            return Health.up()
                .withDetail("database", DATABASE_NAME)
                .withDetail("status", "healthy")
                .withDetail("message", "Database schema and connections verified")
                .withDetail("tables", List.of(REQUIRED_TABLES))
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
