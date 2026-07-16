package org.acme.common.health.postgres;

import javax.sql.DataSource;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.stereotype.Component;

import org.acme.common.health.core.AbstractDatasourceHealthIndicator;

/**
 * Health indicator for PostgreSQL database connectivity.
 * <p>
 * Verifies PostgreSQL connectivity by executing {@code SELECT 1} against the
 * application's {@link DataSource}. Enabled by default; to disable it:
 * <pre>
 * management:
 *   health:
 *     postgres:
 *       enabled: false
 * </pre>
 *
 * @see AbstractDatasourceHealthIndicator
 */
@Component
@ConditionalOnEnabledHealthIndicator("postgres")
public class PostgresHealthIndicator extends AbstractDatasourceHealthIndicator {

    public PostgresHealthIndicator(DataSource dataSource) {
        super(dataSource, "PostgreSQL", "SELECT 1");
    }
}
