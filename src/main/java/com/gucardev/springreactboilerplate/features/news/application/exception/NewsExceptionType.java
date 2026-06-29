package com.gucardev.springreactboilerplate.features.news.application.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.news.not_found", HttpStatus.NOT_FOUND, "NEWS_NOT_FOUND");

    public static final ExceptionType FEATURED_IMAGE_NOT_IN_IMAGES =
            new ExceptionType("error.news.featured_image_not_in_images", HttpStatus.BAD_REQUEST,
                    "NEWS_FEATURED_IMAGE_INVALID");
}
