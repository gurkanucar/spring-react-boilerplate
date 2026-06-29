package com.gucardev.springreactboilerplate.features.news.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link News} domain model and the {@link NewsJpaEntity}. Kept hand-written
 * (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the super-builder and is
 * trivial enough to read at a glance.
 */
@Component
public class NewsPersistenceMapper {

    News toDomain(NewsJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return News.builder()
                .id(entity.getId())
                .workspaceId(entity.getWorkspaceId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .content(entity.getContent())
                .featured(entity.getFeatured())
                .featuredImageId(entity.getFeaturedImageId())
                .imageIds(entity.getImageIds() == null ? new ArrayList<>() : new ArrayList<>(entity.getImageIds()))
                .attachmentIds(entity.getAttachmentIds() == null ? new ArrayList<>() : new ArrayList<>(entity.getAttachmentIds()))
                .tags(entity.getTags() == null ? new HashSet<>() : new HashSet<>(entity.getTags()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    NewsJpaEntity toEntity(News news) {
        if (news == null) {
            return null;
        }
        return NewsJpaEntity.builder()
                .id(news.getId())
                .workspaceId(news.getWorkspaceId())
                .title(news.getTitle())
                .slug(news.getSlug())
                .content(news.getContent())
                .featured(news.getFeatured())
                .featuredImageId(news.getFeaturedImageId())
                .imageIds(news.getImageIds() == null ? new ArrayList<>() : new ArrayList<>(news.getImageIds()))
                .attachmentIds(news.getAttachmentIds() == null ? new ArrayList<>() : new ArrayList<>(news.getAttachmentIds()))
                .tags(news.getTags() == null ? new HashSet<>() : new HashSet<>(news.getTags()))
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .createdBy(news.getCreatedBy())
                .updatedBy(news.getUpdatedBy())
                .build();
    }
}
