package com.gucardev.springreactboilerplate.features.scheduledevent.repository.specification;

import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEvent;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.request.ScheduledEventFilterRequest;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for a {@link ScheduledEventFilterRequest} from the reusable
 * {@link BaseSpecification} predicates. Each predicate is a no-op when its input is empty.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScheduledEventSpecification {

    public static Specification<ScheduledEvent> build(ScheduledEventFilterRequest filter) {
        return BaseSpecification.<ScheduledEvent>equals("status", filter.getStatus())
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }
}
