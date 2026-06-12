package com.gucardev.springreactboilerplate.infra.config.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseWrapper<T>(
        Boolean success,
        Integer status,
        String message,
        T data,
        PageMeta page
) {

    // -------------------------------------------------------------------------
    // Non-paged
    // -------------------------------------------------------------------------

    public static <T> ApiResponseWrapper<T> ok(T data) {
        return new ApiResponseWrapper<>(true, 200, null, data, null);
    }

    public static <T> ApiResponseWrapper<T> ok(T data, String message) {
        return new ApiResponseWrapper<>(true, 200, message, data, null);
    }

    public static <T> ApiResponseWrapper<T> created(T data) {
        return new ApiResponseWrapper<>(true, 201, null, data, null);
    }

    public static <T> ApiResponseWrapper<T> ok() {
        return new ApiResponseWrapper<>(true, 204, null, null, null);
    }

    // -------------------------------------------------------------------------
    // Paged — data becomes List<T> via Page.getContent()
    // -------------------------------------------------------------------------

    public static <T> ApiResponseWrapper<List<T>> ok(Page<T> page) {
        return new ApiResponseWrapper<>(
                true, 200, null,
                page.getContent(),
                PageMeta.from(page)
        );
    }

    public static <T> ApiResponseWrapper<List<T>> ok(Page<T> page, String message) {
        return new ApiResponseWrapper<>(
                true, 200, message,
                page.getContent(),
                PageMeta.from(page)
        );
    }

    // -------------------------------------------------------------------------
    // PageMeta
    // -------------------------------------------------------------------------

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PageMeta(
            Integer currentPage,
            Integer pageSize,
            Long totalElements,
            Integer totalPages,
            Boolean first,
            Boolean last,
            Boolean hasNext,
            Boolean hasPrevious,
            Boolean empty
    ) {
        public static PageMeta from(Page<?> page) {
            return new PageMeta(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast(),
                    page.hasNext(),
                    page.hasPrevious(),
                    page.isEmpty()
            );
        }
    }
}