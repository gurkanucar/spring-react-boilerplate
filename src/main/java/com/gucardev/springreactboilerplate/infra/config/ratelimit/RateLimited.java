package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Per-key method-level rate limit, enforced by {@link RateLimitedAspect} on any Spring bean method.
 * A token bucket of {@link #capacity} tokens refills fully every {@link #refillSeconds}, scoped to
 * the resolved {@link Key} (and the annotated method). Over-limit calls throw a 429 BusinessException.
 *
 * <pre>{@code
 * @RateLimited(key = Key.USER, capacity = 5, refillSeconds = 60)
 * public Report generate(...) { ... }   // each user: 5 generations / minute
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    Key key() default Key.USER;

    int capacity() default 10;

    int refillSeconds() default 60;

    enum Key {
        /** The authenticated user id; fails with 403 if there is no authenticated user. */
        USER,
        /** The caller's IP (X-Forwarded-For first hop, else remote address). */
        IP,
        /** The user id when authenticated, otherwise the IP. */
        USER_OR_IP
    }
}
