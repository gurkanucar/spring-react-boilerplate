package com.gucardev.springreactboilerplate.features.scheduledevent.adapter.in.web;

import com.gucardev.springreactboilerplate.features.scheduledevent.adapter.in.web.dto.ScheduleEventRequest;
import com.gucardev.springreactboilerplate.features.scheduledevent.adapter.in.web.dto.ScheduledEventFilterRequest;
import com.gucardev.springreactboilerplate.features.scheduledevent.adapter.in.web.dto.ScheduledEventResponse;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.CancelScheduledEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.GetScheduledEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ListScheduledEventsUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduleEventCommand;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduleEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduledEventCriteria;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving (web) adapter demonstrating RabbitMQ scheduled/delayed events backed by a persisted
 * {@code ScheduledEvent} row: schedule one, read its lifecycle state, list them, and cancel a pending
 * one (the broker still delivers the in-flight message, but the listener skips a cancelled row).
 *
 * <p>The controller only talks to input ports and maps between web DTOs and the domain model.
 */
@RestController
@RequestMapping("/api/v1/scheduled-events")
@RequiredArgsConstructor
@Tag(name = "Scheduled Events", description = "Publish events that RabbitMQ delivers after a delay.")
public class ScheduledEventController {

    private final ScheduleEventUseCase scheduleEventUseCase;
    private final GetScheduledEventUseCase getScheduledEventUseCase;
    private final ListScheduledEventsUseCase listScheduledEventsUseCase;
    private final CancelScheduledEventUseCase cancelScheduledEventUseCase;
    private final ScheduledEventWebMapper scheduledEventWebMapper;

    @Operation(summary = "Schedule an event to be delivered after delaySeconds")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ScheduledEventResponse>> schedule(
            @Valid @RequestBody ScheduleEventRequest request) {
        ScheduledEventResponse response = scheduledEventWebMapper.toResponse(scheduleEventUseCase.schedule(
                new ScheduleEventCommand(request.eventType(), request.payload(), request.delaySeconds())));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Get a scheduled event's details and lifecycle status")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ScheduledEventResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                scheduledEventWebMapper.toResponse(getScheduledEventUseCase.getById(id))));
    }

    @Operation(summary = "List scheduled events (paged, optional status filter)")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<ScheduledEventResponse>>> list(
            @Valid ScheduledEventFilterRequest filter) {
        Page<ScheduledEventResponse> page = listScheduledEventsUseCase
                .list(toCriteria(filter), filter.toPageable())
                .map(scheduledEventWebMapper::toResponse);
        return ResponseEntity.ok(ApiResponseWrapper.ok(page));
    }

    @Operation(summary = "Cancel a still-pending scheduled event")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ScheduledEventResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                scheduledEventWebMapper.toResponse(cancelScheduledEventUseCase.cancel(id))));
    }

    private ScheduledEventCriteria toCriteria(ScheduledEventFilterRequest filter) {
        return new ScheduledEventCriteria(filter.getStatus(), filter.getStartDate(), filter.getEndDate());
    }
}
