package com.gucardev.springreactboilerplate.features.scheduledevent.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduledEventCriteria;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for a {@link ScheduledEventCriteria} from the reusable
 * {@link BaseSpecification} predicates. Each predicate is a no-op when its input is empty. Lives in
 * the persistence adapter because it is a JPA concern over {@link ScheduledEventJpaEntity}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScheduledEventSpecification {

    public static Specification<ScheduledEventJpaEntity> build(ScheduledEventCriteria criteria) {
        return BaseSpecification.<ScheduledEventJpaEntity>equals("status", criteria.status())
                .and(BaseSpecification.createdBetween(criteria.startDate(), criteria.endDate()));
    }
}
