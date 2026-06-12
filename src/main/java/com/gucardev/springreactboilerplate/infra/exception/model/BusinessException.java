package com.gucardev.springreactboilerplate.infra.exception.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String businessErrorCode;

    public BusinessException(String message, HttpStatus status, String businessErrorCode) {
        super(message);
        this.status = status;
        this.businessErrorCode = businessErrorCode;
    }

}