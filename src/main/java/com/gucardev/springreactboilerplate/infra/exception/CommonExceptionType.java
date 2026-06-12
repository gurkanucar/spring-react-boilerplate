package com.gucardev.springreactboilerplate.infra.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum CommonExceptionType implements ExceptionType {

    DEFAULT("error.default", HttpStatus.BAD_REQUEST, "1000"),
    NOT_FOUND("error.not_found", HttpStatus.NOT_FOUND, "NOT_FOUND"),
    ALREADY_EXISTS("error.already_exists", HttpStatus.CONFLICT, "ALREADY_EXISTS"),
    VALIDATION_FAILED("error.validation_failed", HttpStatus.BAD_REQUEST, "VALIDATION_FAILED"),
    FORBIDDEN("error.auth.forbidden", HttpStatus.FORBIDDEN, "ACCESS_DENIED");


    private final String key;
    private final HttpStatus status;
    private final String code;
}