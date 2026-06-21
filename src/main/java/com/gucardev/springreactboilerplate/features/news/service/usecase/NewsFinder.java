package com.gucardev.springreactboilerplate.features.news.service.usecase;

import com.gucardev.springreactboilerplate.features.news.entity.News;
import com.gucardev.springreactboilerplate.features.news.exception.NewsExceptionType;
import com.gucardev.springreactboilerplate.features.news.repository.NewsRepository;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantExceptionType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsFinder {

    private final NewsRepository repository;

    /** Loads a news entry, ensuring it belongs to the active workspace; reports a cross-workspace hit as NOT_FOUND. */
    public News findById(UUID id) {
        News news = repository.findById(id)
                .orElseThrow(() -> NewsExceptionType.NOT_FOUND.toException(id));
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        if (!news.getWorkspaceId().equals(workspaceId)) {
            throw TenantExceptionType.CROSS_WORKSPACE.toException();
        }
        return news;
    }
}
