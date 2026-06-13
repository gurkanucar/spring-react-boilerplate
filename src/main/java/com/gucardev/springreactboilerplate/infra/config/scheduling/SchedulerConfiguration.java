package com.gucardev.springreactboilerplate.infra.config.scheduling;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;

/**
 * Central scheduling configuration. Enables Spring's {@code @Scheduled} support backed by a small
 * dedicated thread pool (so jobs don't serialize on the default single-threaded scheduler) with an
 * error handler, and enables ShedLock so every {@code @SchedulerLock}-annotated job runs on a single
 * instance at a time (safe to deploy multiple replicas).
 *
 * <p>{@code defaultLockAtMostFor} is the safety net: if a node dies mid-job, the lock is released
 * after this window. Individual jobs can override it. Locks are stored in the {@code shedlock} table
 * (see {@link ShedLockEntity}) on the application datasource; {@code usingDbTime()} relies on the
 * database clock to avoid skew between instances.
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
public class SchedulerConfiguration implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("Scheduler-");
        scheduler.setErrorHandler(schedulerErrorHandler());
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public ErrorHandler schedulerErrorHandler() {
        return throwable -> log.error("[SCHEDULER] exception occurred", throwable);
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build());
    }
}
