package com.gucardev.springreactboilerplate.infra.config.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds the {@code security.jwt.*} block from application.yml.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    /** HMAC signing secret. Must be at least 32 bytes for HS256. */
    private String secretKey;

    /** Access-token lifetime, in minutes. */
    private long tokenValidityInMinutes;

    /** Refresh-token lifetime, in minutes. */
    private long refreshTokenValidityInMinutes;

    /** Spring cron expression for purging expired/revoked refresh tokens. */
    private String refreshTokenCleanupCron;
}
