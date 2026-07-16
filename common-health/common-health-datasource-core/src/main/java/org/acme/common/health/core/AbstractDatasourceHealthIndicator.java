package org.acme.common.health.core;

import javax.sql.DataSource;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * Abstract base class for health indicators that verify database connectivity
 * by executing a validation query.
 * <p>
 * Subclasses supply the database name and validation query via the constructor;
 * this class executes the query with a bounded timeout and reports the result
 * with timing details. A failed or timed-out query propagates as an exception,
 * which {@link AbstractHealthIndicator} converts into a {@code DOWN} status.
 * <p>
 * Example usage:
 * <pre>
 * public class MyDatabaseHealthIndicator extends AbstractDatasourceHealthIndicator {
 *     public MyDatabaseHealthIndicator(DataSource dataSource) {
 *         super(dataSource, "MyDatabase", "SELECT 1");
 *     }
 * }
 * </pre>
 *
 * @see AbstractHealthIndicator
 */
public abstract class AbstractDatasourceHealthIndicator extends AbstractHealthIndicator {

    private static final int DEFAULT_QUERY_TIMEOUT_SECONDS = 2;

    private final JdbcTemplate jdbcTemplate;
    private final String databaseName;
    private final String validationQuery;

    /**
     * Creates an indicator with the default query timeout of
     * {@value #DEFAULT_QUERY_TIMEOUT_SECONDS} seconds.
     *
     * @param dataSource the datasource to check; must not be {@code null}
     * @param databaseName human-readable name reported in the health details;
     *     must not be blank
     * @param validationQuery lightweight query used to verify connectivity;
     *     must not be blank
     */
    protected AbstractDatasourceHealthIndicator(
        DataSource dataSource,
        String databaseName,
        String validationQuery
    ) {
        this(dataSource, databaseName, validationQuery, DEFAULT_QUERY_TIMEOUT_SECONDS);
    }

    /**
     * Creates an indicator with a custom query timeout.
     *
     * @param dataSource the datasource to check; must not be {@code null}
     * @param databaseName human-readable name reported in the health details;
     *     must not be blank
     * @param validationQuery lightweight query used to verify connectivity;
     *     must not be blank
     * @param queryTimeoutSeconds maximum time to wait for the validation query
     *     before failing the health check; must be positive
     */
    protected AbstractDatasourceHealthIndicator(
        DataSource dataSource,
        String databaseName,
        String validationQuery,
        int queryTimeoutSeconds
    ) {
        Assert.notNull(dataSource, "DataSource must not be null");
        Assert.hasText(databaseName, "Database name must not be blank");
        Assert.hasText(validationQuery, "Validation query must not be blank");
        Assert.isTrue(queryTimeoutSeconds > 0, "Query timeout must be positive");

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setQueryTimeout(queryTimeoutSeconds);
        this.databaseName = databaseName;
        this.validationQuery = validationQuery;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        long startNanos = System.nanoTime();

        jdbcTemplate.execute(validationQuery);

        long durationNanos = System.nanoTime() - startNanos;
        double durationMs = roundToThreeDecimalPlaces(durationNanos / 1_000_000.0);

        builder.up()
            .withDetail("database", databaseName)
            .withDetail("queryDurationMs", durationMs);
    }

    private static double roundToThreeDecimalPlaces(double value) {
        return Math.round(value * 1_000.0) / 1_000.0;
    }
}
