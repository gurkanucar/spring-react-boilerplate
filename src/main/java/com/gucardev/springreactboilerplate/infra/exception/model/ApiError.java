package com.gucardev.springreactboilerplate.infra.exception.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response envelope returned on all non-2xx responses.")
public record ApiError(
        @Schema(description = "Always false for error responses", example = "false")
        Boolean success,
        @Schema(description = "HTTP status code", example = "404")
        Integer status,
        @Schema(description = "Distributed trace ID for log correlation", example = "a3f1b2c4-...")
        String traceId,
        @Schema(description = "Human-readable error message", example = "User with id ... not found!")
        String message,
        @Schema(description = "Machine-readable error code", example = "USER_NOT_FOUND")
        String businessErrorCode,
        @Schema(description = "Per-field validation errors, present only for 400 validation failures")
        Map<String, String> validationErrors

) {

    public static ApiError unexpected(String message, String traceId) {
        return new ApiError(false, 500, traceId, message, null, null);
    }

    public static ApiError business(Integer status, String message,
                                    String businessErrorCode, String traceId) {
        return new ApiError(false, status, traceId, message, businessErrorCode, null);
    }

    public static ApiError validation(Map<String, String> errors, String message, String traceId) {
        return new ApiError(false, 400, traceId, message, "VALIDATION_FAILED", errors);
    }

}
