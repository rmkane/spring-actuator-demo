package org.acme.common.health.postgres.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan("org.acme.common.health.postgres")
public class PostgresHealthAutoConfiguration {
}
