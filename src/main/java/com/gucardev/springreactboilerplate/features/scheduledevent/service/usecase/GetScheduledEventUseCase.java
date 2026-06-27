package com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase;

import com.gucardev.springreactboilerplate.features.scheduledevent.mapper.ScheduledEventMapper;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.dto.ScheduledEventResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetScheduledEventUseCase {

    private final ScheduledEventFinder finder;
    private final ScheduledEventMapper mapper;

    @Transactional(readOnly = true)
    public ScheduledEventResponse execute(UUID id) {
        return mapper.toDto(finder.findById(id));
    }
}
