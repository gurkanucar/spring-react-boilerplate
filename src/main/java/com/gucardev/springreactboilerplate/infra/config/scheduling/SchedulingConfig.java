package com.gucardev.springreactboilerplate.infra.config.scheduling;

import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's {@code @Scheduled} support plus ShedLock, so every {@code @SchedulerLock}-
 * annotated job runs on a single instance at a time (safe to deploy multiple replicas).
 *
 * <p>{@code defaultLockAtMostFor} is the safety net: if a node dies mid-job, the lock is released
 * after this window. Individual jobs can override it. Locks are stored in the {@code shedlock}
 * table (see {@link ShedLockEntity}) on the application datasource; {@code usingDbTime()} relies on
 * the database clock to avoid skew between instances.
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
public class SchedulingConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build());
    }
}
