package com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase;

import com.gucardev.springreactboilerplate.features.scheduledevent.mapper.ScheduledEventMapper;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.dto.ScheduledEventResponse;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.request.ScheduledEventFilterRequest;
import com.gucardev.springreactboilerplate.features.scheduledevent.repository.ScheduledEventRepository;
import com.gucardev.springreactboilerplate.features.scheduledevent.repository.specification.ScheduledEventSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListScheduledEventsUseCase {

    private final ScheduledEventRepository repository;
    private final ScheduledEventMapper mapper;

    @Transactional(readOnly = true)
    public Page<ScheduledEventResponse> execute(ScheduledEventFilterRequest filter) {
        return repository.findAll(ScheduledEventSpecification.build(filter), filter.toPageable())
                .map(mapper::toDto);
    }
}
