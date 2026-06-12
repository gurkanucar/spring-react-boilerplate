package com.gucardev.springreactboilerplate.infra.exception;


import com.gucardev.springreactboilerplate.infra.config.message.MessageUtil;
import com.gucardev.springreactboilerplate.infra.exception.model.BusinessException;
import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import org.springframework.http.HttpStatus;

public final class ExceptionUtil {

    private ExceptionUtil() {}

    // ==================== Generic ====================

    public static BusinessException of(ExceptionType type) {
        return new BusinessException(
                MessageUtil.getMessage(type.getKey()),
                type.getStatus(),
                type.getCode()
        );
    }

    public static BusinessException of(ExceptionType type, Object... args) {
        return new BusinessException(
                MessageUtil.getMessage(type.getKey(), args),
                type.getStatus(),
                type.getCode()
        );
    }

    // ==================== Convenience Methods ====================

    public static BusinessException notFound(String entity, Object id) {
        return of(CommonExceptionType.NOT_FOUND, entity, id);
    }

    public static BusinessException alreadyExists(String what) {
        return of(CommonExceptionType.ALREADY_EXISTS, what);
    }

    public static BusinessException alreadyExists(String entity, String field, Object value) {
        return new BusinessException(
                MessageUtil.getMessage("error.already_exists.detail", new Object[]{entity, field, value}),
                HttpStatus.CONFLICT,
                "ALREADY_EXISTS"
        );
    }
}