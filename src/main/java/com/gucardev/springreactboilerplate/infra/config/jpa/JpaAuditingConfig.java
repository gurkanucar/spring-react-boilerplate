package com.gucardev.springreactboilerplate.infra.config.jpa;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Enables JPA auditing so {@code @CreatedDate}/{@code @LastModifiedDate} populate
 * automatically, and supplies the {@code @CreatedBy}/{@code @LastModifiedBy} value from the
 * current security principal (falling back to {@code "system"} for unauthenticated work
 * such as scheduled jobs or startup seeding).
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    private static final String SYSTEM = "system";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication instanceof AnonymousAuthenticationToken) {
                return Optional.of(SYSTEM);
            }
            return Optional.ofNullable(authentication.getName());
        };
    }
}
