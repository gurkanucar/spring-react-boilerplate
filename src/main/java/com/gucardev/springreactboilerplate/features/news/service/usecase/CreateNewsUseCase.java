package com.gucardev.springreactboilerplate.features.news.service.usecase;

import com.gucardev.springreactboilerplate.features.news.entity.News;
import com.gucardev.springreactboilerplate.features.news.exception.NewsExceptionType;
import com.gucardev.springreactboilerplate.features.news.mapper.NewsMapper;
import com.gucardev.springreactboilerplate.features.news.model.dto.NewsResponseDto;
import com.gucardev.springreactboilerplate.features.news.model.request.CreateNewsRequest;
import com.gucardev.springreactboilerplate.features.news.repository.NewsRepository;
import com.gucardev.springreactboilerplate.features.shared.event.NotificationEvent;
import com.gucardev.springreactboilerplate.features.shared.util.SlugUtil;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateNewsUseCase {

    private final NewsRepository repository;
    private final NewsMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public NewsResponseDto execute(CreateNewsRequest request) {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        News news = mapper.toEntity(request);
        news.setWorkspaceId(workspaceId);
        news.setFeatured(request.featured() != null && request.featured());
        news.setSlug(generateUniqueSlug(request.title(), workspaceId));
        if (!news.isFeaturedImageConsistent()) {
            throw NewsExceptionType.FEATURED_IMAGE_NOT_IN_IMAGES.toException();
        }
        News saved = repository.save(news);
        notifyAuthor(workspaceId, saved);
        return mapper.toDto(saved);
    }

    /**
     * Example of the event-driven notification flow: tell the author their news was created. The
     * notification module gates this on the workspace's IN_APP_NOTIFICATIONS flag. Skipped when there
     * is no authenticated user (e.g. system/seed calls).
     */
    private void notifyAuthor(UUID workspaceId, News news) {
        UUID authorId = SecurityUtils.currentUserIdOrNull();
        if (authorId != null) {
            eventPublisher.publishEvent(new NotificationEvent(workspaceId, authorId,
                    "NEWS_CREATED", "News published", news.getTitle()));
        }
    }

    private String generateUniqueSlug(String title, UUID workspaceId) {
        String base = SlugUtil.toSlug(title);
        if (base.isBlank()) {
            base = "news";
        }
        String slug = base;
        int suffix = 2;
        while (repository.existsByWorkspaceIdAndSlug(workspaceId, slug)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }
}
