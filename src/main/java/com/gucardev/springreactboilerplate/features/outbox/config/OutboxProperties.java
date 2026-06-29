package com.gucardev.springreactboilerplate.features.outbox.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds the {@code outbox.*} block from application.yml — tunables for the relay poller.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "outbox")
public class OutboxProperties {

    /** Delay between relay polls (fixedDelay), in milliseconds. */
    private long pollDelayMs = 2000;

    /** Max rows the relay claims per poll. */
    private int batchSize = 50;

    /** Publish attempts before a row is given up on and marked FAILED. */
    private int maxAttempts = 5;

    /** Base backoff after a failed publish, in seconds; multiplied by the attempt count. */
    private long backoffSeconds = 30;
}
