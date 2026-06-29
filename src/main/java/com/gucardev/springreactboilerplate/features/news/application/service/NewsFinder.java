package com.gucardev.springreactboilerplate.features.news.application.service;

import com.gucardev.springreactboilerplate.features.news.application.exception.NewsExceptionType;
import com.gucardev.springreactboilerplate.features.news.application.port.out.LoadNewsPort;
import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantExceptionType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup for news, used by the read/update/delete use cases.
 */
@Service
@RequiredArgsConstructor
public class NewsFinder {

    private final LoadNewsPort loadNewsPort;

    /** Loads a news entry, ensuring it belongs to the active workspace; reports a cross-workspace hit as NOT_FOUND. */
    public News findById(UUID id) {
        News news = loadNewsPort.findById(id)
                .orElseThrow(() -> NewsExceptionType.NOT_FOUND.toException(id));
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        if (!news.getWorkspaceId().equals(workspaceId)) {
            throw TenantExceptionType.CROSS_WORKSPACE.toException();
        }
        return news;
    }
}
