package com.gucardev.springreactboilerplate.features.news.repository.specification;

import com.gucardev.springreactboilerplate.features.news.entity.News;
import com.gucardev.springreactboilerplate.features.news.model.request.NewsFilterRequest;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class NewsSpecification {

    private NewsSpecification() {
    }

    /**
     * @param workspaceId the tenant scope to constrain to; never {@code null} (news is always
     *                    resolved within an active workspace).
     */
    public static Specification<News> build(NewsFilterRequest filter, UUID workspaceId) {
        return BaseSpecification.<News>equals("workspaceId", workspaceId)
                .and(BaseSpecification.like("title", filter.getTitle()))
                .and(BaseSpecification.equals("featured", filter.getFeatured()))
                .and(hasTag(filter.getTag()))
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }

    private static Specification<News> hasTag(String tag) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(tag)) {
                return null;
            }
            if (query != null) {
                query.distinct(true);
            }
            return cb.isMember(tag, root.<java.util.Collection<String>>get("tags"));
        };
    }
}
