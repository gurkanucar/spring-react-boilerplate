package com.gucardev.springreactboilerplate.domain.organization.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrganizationExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.organization.not_found", HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND");

    public static final ExceptionType SLUG_ALREADY_EXISTS =
            new ExceptionType("error.organization.slug_exists", HttpStatus.CONFLICT, "ORGANIZATION_SLUG_EXISTS");
}
