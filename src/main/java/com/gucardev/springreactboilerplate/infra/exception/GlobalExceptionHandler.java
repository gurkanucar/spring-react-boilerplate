package com.gucardev.springreactboilerplate.infra.exception;

import com.gucardev.springreactboilerplate.infra.config.message.MessageUtil;
import com.gucardev.springreactboilerplate.infra.exception.model.ApiError;
import com.gucardev.springreactboilerplate.infra.exception.model.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // -------------------------------------------------------------------------
    // Business
    // -------------------------------------------------------------------------

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(
            BusinessException ex, HttpServletRequest request) {

        String message = MessageUtil.getMessage(ex.getMessageKey(), ex.getArgs());

        log.warn("[BUSINESS] code={} key={} path={}",
                ex.getCode(), ex.getMessageKey(), request.getRequestURI());

        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiError.business(
                        ex.getStatus().value(),
                        message,
                        ex.getCode(),
                        getTraceId()));
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        this::friendlyFieldErrorMessage,
                        (a, b) -> a));

        log.warn("[VALIDATION] fields={}", fieldErrors.keySet());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.validation(fieldErrors, MessageUtil.getMessage("error.validation_failed"), getTraceId()));
    }

    /**
     * Replace Spring's default "Failed to convert from type [java.lang.String] to type [...]" wall
     * of text with something a developer can act on. For enum-typed fields, list the allowed
     * values inline so the caller sees exactly what to send.
     */
    private String friendlyFieldErrorMessage(FieldError error) {
        org.springframework.beans.TypeMismatchException tme = error.contains(org.springframework.beans.TypeMismatchException.class)
                ? error.unwrap(org.springframework.beans.TypeMismatchException.class)
                : null;
        if (tme != null && tme.getRequiredType() != null && tme.getRequiredType().isEnum()) {
            Object[] constants = tme.getRequiredType().getEnumConstants();
            String allowed = java.util.Arrays.stream(constants)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            Object rejected = tme.getValue() != null ? tme.getValue() : error.getRejectedValue();
            return "Invalid value '" + rejected + "' for "
                    + tme.getRequiredType().getSimpleName()
                    + ". Allowed: [" + allowed + "]";
        }
        return error.getDefaultMessage() != null ? error.getDefaultMessage() : "invalid";
    }

    // -------------------------------------------------------------------------
    // Security
    // -------------------------------------------------------------------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(HttpServletRequest request) {
        log.warn("[ACCESS_DENIED] path={}", request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiError.business(403,
                        MessageUtil.getMessage("error.auth.forbidden"),
                        "ACCESS_DENIED", getTraceId()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(HttpServletRequest request) {
        log.warn("[AUTH] Bad credentials path={}", request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.business(401,
                        MessageUtil.getMessage("error.auth.invalid_credentials"),
                        "AUTHENTICATION_FAILED", getTraceId()));
    }

    @ExceptionHandler({LockedException.class, DisabledException.class})
    public ResponseEntity<ApiError> handleAccountStatus(
            RuntimeException ex, HttpServletRequest request) {

        boolean locked = ex instanceof LockedException;
        log.warn("[AUTH] Account {} path={}", locked ? "locked" : "disabled",
                request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.business(401,
                        MessageUtil.getMessage(locked ? "error.auth.account_locked" : "error.auth.account_disabled"),
                        locked ? "ACCOUNT_LOCKED" : "ACCOUNT_DISABLED",
                        getTraceId()));
    }

    // -------------------------------------------------------------------------
    // JPA / persistence
    // -------------------------------------------------------------------------

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        log.warn("[NOT_FOUND] {} path={}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.business(404,
                        MessageUtil.getMessage("error.resource.not.found"),
                        "RESOURCE_NOT_FOUND", getTraceId()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        // full cause logged — never exposed
        log.error("[DB_INTEGRITY] path={} cause={}",
                request.getRequestURI(), extractRootCause(ex));
        log.error("[DB_INTEGRITY] Full trace:", ex);

        boolean isDuplicate = extractRootCause(ex).toLowerCase()
                .matches(".*(unique|duplicate|uk_).*");

        return ResponseEntity
                .status(isDuplicate ? HttpStatus.CONFLICT : HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError.business(
                        isDuplicate ? 409 : 422,
                        MessageUtil.getMessage(isDuplicate ? "error.duplicate_resource" : "error.data_integrity"),
                        isDuplicate ? "DUPLICATE_RESOURCE" : "DATA_INTEGRITY_VIOLATION",
                        getTraceId()));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLocking(
            OptimisticLockingFailureException ex, HttpServletRequest request) {

        log.warn("[DB_LOCK] path={} msg={}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.business(409,
                        MessageUtil.getMessage("error.concurrent_modification"),
                        "CONCURRENT_MODIFICATION", getTraceId()));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiError> handleSql(
            SQLException ex, HttpServletRequest request) {

        // SQLState and error code logged — never exposed
        log.error("[SQL] SQLState={} ErrorCode={} path={} msg={}",
                ex.getSQLState(), ex.getErrorCode(),
                request.getRequestURI(), ex.getMessage());
        log.error("[SQL] Full trace:", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.unexpected(MessageUtil.getMessage("error.unexpected"), getTraceId()));
    }

    // -------------------------------------------------------------------------
    // Common web
    // -------------------------------------------------------------------------

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("[NOT_FOUND] {} {}", ex.getHttpMethod(), ex.getRequestURL());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body((Object) ApiError.business(404,
                        MessageUtil.getMessage("error.endpoint_not_found",
                                new Object[]{ex.getHttpMethod(), ex.getRequestURL()}),
                        "ENDPOINT_NOT_FOUND", getTraceId()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("[METHOD_NOT_ALLOWED] {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body((Object) ApiError.business(405,
                        MessageUtil.getMessage("error.method_not_allowed", new Object[]{ex.getMethod()}),
                        "METHOD_NOT_ALLOWED", getTraceId()));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("[MISSING_PARAM] param={}", ex.getParameterName());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.business(400,
                        MessageUtil.getMessage("error.missing_parameter", new Object[]{ex.getParameterName()}),
                        "MISSING_PARAMETER", getTraceId()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String expected = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName() : "unknown";

        log.warn("[TYPE_MISMATCH] param={} expected={} got={}",
                ex.getName(), expected, ex.getValue());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.business(400,
                        MessageUtil.getMessage("error.type_mismatch",
                                new Object[]{ex.getName(), expected, ex.getValue()}),
                        "TYPE_MISMATCH", getTraceId()));
    }

    // -------------------------------------------------------------------------
    // Catch-all
    // -------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception ex, HttpServletRequest request) {

        log.error("[ERR-{}] {} on {} {}",
                getTraceId(), ex.getClass().getSimpleName(),
                request.getMethod(), request.getRequestURI());
        log.error("[ERR-{}] Full trace:", getTraceId(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.unexpected(MessageUtil.getMessage("error.unexpected"), getTraceId()));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String getTraceId() {
        return MDC.get("traceId");
    }

    private String extractRootCause(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() != null ? cause.getMessage() : "";
    }
}