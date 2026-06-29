package com.gucardev.springreactboilerplate.features.scheduledevent.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduledEventCriteria;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.LoadScheduledEventPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.SaveScheduledEventPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.SearchScheduledEventsPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the scheduled-event load/save/search output ports with Spring Data JPA. Maps
 * domain ⇄ entity at the boundary so the application core stays persistence-agnostic, and owns the
 * {@code Specification} translation for the search.
 */
@Component
@RequiredArgsConstructor
public class ScheduledEventPersistenceAdapter
        implements LoadScheduledEventPort, SaveScheduledEventPort, SearchScheduledEventsPort {

    private final ScheduledEventJpaRepository repository;
    private final ScheduledEventPersistenceMapper mapper;

    @Override
    public Optional<ScheduledEvent> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public ScheduledEvent save(ScheduledEvent event) {
        return mapper.toDomain(repository.save(mapper.toEntity(event)));
    }

    @Override
    public Page<ScheduledEvent> search(ScheduledEventCriteria criteria, Pageable pageable) {
        return repository.findAll(ScheduledEventSpecification.build(criteria), pageable)
                .map(mapper::toDomain);
    }
}
