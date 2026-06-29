package com.gucardev.springreactboilerplate.features.news.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.news.application.port.out.NewsSearchCriteria;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsSpecification {

    /**
     * Builds a {@link Specification} for the {@link NewsJpaEntity} from the application-level
     * {@link NewsSearchCriteria}. The {@code workspaceId} is always present (news is always resolved
     * within an active workspace).
     */
    public static Specification<NewsJpaEntity> build(NewsSearchCriteria criteria) {
        return BaseSpecification.<NewsJpaEntity>equals("workspaceId", criteria.workspaceId())
                .and(BaseSpecification.like("title", criteria.title()))
                .and(BaseSpecification.equals("featured", criteria.featured()))
                .and(hasTag(criteria.tag()))
                .and(BaseSpecification.createdBetween(criteria.startDate(), criteria.endDate()));
    }

    private static Specification<NewsJpaEntity> hasTag(String tag) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(tag)) {
                return null;
            }
            query.distinct(true);
            return cb.isMember(tag, root.get("tags"));
        };
    }
}
