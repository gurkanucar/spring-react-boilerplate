package com.gucardev.springreactboilerplate.infra.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TenantExceptionType implements ExceptionType {

    NO_WORKSPACE_CONTEXT("error.tenant.no_workspace_context", HttpStatus.FORBIDDEN, "NO_WORKSPACE_CONTEXT"),
    CROSS_WORKSPACE("error.tenant.cross_workspace", HttpStatus.NOT_FOUND, "CROSS_WORKSPACE"),
    NO_ORGANIZATION_CONTEXT("error.tenant.no_organization_context", HttpStatus.FORBIDDEN, "NO_ORGANIZATION_CONTEXT"),
    CROSS_ORGANIZATION("error.tenant.cross_organization", HttpStatus.NOT_FOUND, "CROSS_ORGANIZATION");

    private final String key;
    private final HttpStatus status;
    private final String code;
}
