package com.gucardev.springreactboilerplate.features.shared.repository.specification;

import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BaseSpecification {

    public static <T> Specification<T> like(String fieldName, String value) {
        return like(fieldName, value, Locale.of("tr", "TR"));
    }

    public static <T> Specification<T> like(String fieldName, String value, Locale locale) {
        return (root, query, cb) -> {
            if (value == null || value.isEmpty())
                return null;
            Expression<String> fieldExpression = root.get(fieldName);
            Expression<String> lowerField = cb.lower(fieldExpression);
            String lowerCaseValue = value.toLowerCase(locale);
            return cb.like(lowerField, "%" + lowerCaseValue + "%");
        };
    }

    public static <T> Specification<T> equals(String fieldName, Object value) {
        return (root, query, cb) -> {
            if (value == null)
                return null;
            return cb.equal(root.get(fieldName), value);
        };
    }

    public static <E, I> Specification<E> byId(I id) {
        return (root, query, cb) -> {
            if (id == null)
                return null;
            return cb.equal(root.get("id"), id);
        };
    }

    public static <T> Specification<T> byIds(List<?> ids) {
        return (root, query, cb) -> {
            if (ids == null || ids.isEmpty())
                return null;
            return root.get("id").in(ids);
        };
    }

    public static <T> Specification<T> createdBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null || end == null)
                return null;
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(23, 59, 59, 999999999);
            return cb.between(root.get("createdAt"), startDateTime, endDateTime);
        };
    }

    public static <T> Specification<T> byWorkspaceId(UUID workspaceId) {
        return (root, query, cb) -> {
            if (workspaceId == null)
                return null;
            return cb.equal(root.get("workspace").get("id"), workspaceId);
        };
    }

    public static <T> Specification<T> byNestedId(String relationField, UUID id) {
        return (root, query, cb) -> {
            if (id == null)
                return null;
            return cb.equal(root.get(relationField).get("id"), id);
        };
    }
}
