package com.gucardev.springreactboilerplate.features.news.application.port.out;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Plain application-level filter contract for searching news. Built by a driving adapter from its
 * request DTO; the {@code workspaceId} is supplied by the application service from the active tenant.
 * The persistence adapter translates it into a JPA {@code Specification}.
 */
public record NewsSearchCriteria(
        UUID workspaceId,
        String title,
        Boolean featured,
        String tag,
        LocalDate startDate,
        LocalDate endDate
) {

    /** Returns a copy scoped to the given workspace; used by the service to apply the active tenant. */
    public NewsSearchCriteria withWorkspaceId(UUID workspaceId) {
        return new NewsSearchCriteria(workspaceId, title, featured, tag, startDate, endDate);
    }
}
