package com.gucardev.springreactboilerplate.features.core.file.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain-specific error catalog for the file/storage slice.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.file.not_found", HttpStatus.NOT_FOUND, "FILE_NOT_FOUND");

    public static final ExceptionType EMPTY_FILE =
            new ExceptionType("error.file.empty", HttpStatus.BAD_REQUEST, "FILE_EMPTY");

    public static final ExceptionType FILE_TOO_LARGE =
            new ExceptionType("error.file.too_large", HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE");

    public static final ExceptionType EXTENSION_NOT_ALLOWED =
            new ExceptionType("error.file.extension_not_allowed", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE_EXTENSION_NOT_ALLOWED");

    public static final ExceptionType CONTENT_TYPE_MISMATCH =
            new ExceptionType("error.file.content_mismatch", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE_CONTENT_MISMATCH");

    public static final ExceptionType STORAGE_NOT_CONFIGURED =
            new ExceptionType("error.file.storage_not_configured", HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE_NOT_CONFIGURED");

    public static final ExceptionType STORAGE_BACKEND_NOT_ACTIVE =
            new ExceptionType("error.file.storage_backend_not_active", HttpStatus.BAD_REQUEST, "STORAGE_BACKEND_NOT_ACTIVE");

    public static final ExceptionType STORAGE_FAILURE =
            new ExceptionType("error.file.storage_failure", HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE_FAILURE");

    public static final ExceptionType NOT_AN_IMAGE =
            new ExceptionType("error.file.not_an_image", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE_NOT_AN_IMAGE");

    public static final ExceptionType IMAGE_PROCESSING_FAILED =
            new ExceptionType("error.file.image_processing_failed", HttpStatus.UNPROCESSABLE_ENTITY, "IMAGE_PROCESSING_FAILED");
}
