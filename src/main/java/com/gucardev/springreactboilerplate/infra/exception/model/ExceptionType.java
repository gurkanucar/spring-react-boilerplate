package com.gucardev.springreactboilerplate.infra.exception.model;

import org.springframework.http.HttpStatus;

public interface ExceptionType {
    String getKey();
    HttpStatus getStatus();
    String getCode();
}