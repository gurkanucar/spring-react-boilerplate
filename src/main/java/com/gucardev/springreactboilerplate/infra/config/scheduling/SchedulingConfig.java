package com.gucardev.springreactboilerplate.infra.config.scheduling;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's {@code @Scheduled} support (used by {@code RefreshTokenCleanupJob}).
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
