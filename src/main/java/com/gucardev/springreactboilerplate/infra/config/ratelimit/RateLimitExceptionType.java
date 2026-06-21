package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RateLimitExceptionType {

    public static final ExceptionType RATE_LIMIT_EXCEEDED =
            new ExceptionType("error.rate_limit_exceeded", HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED");
}
