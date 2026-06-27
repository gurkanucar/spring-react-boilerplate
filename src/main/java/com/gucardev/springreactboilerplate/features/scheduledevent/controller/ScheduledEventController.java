package com.gucardev.springreactboilerplate.features.scheduledevent.controller;

import com.gucardev.springreactboilerplate.features.scheduledevent.model.dto.ScheduledEventResponse;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.request.ScheduleEventRequest;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.request.ScheduledEventFilterRequest;
import com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase.CancelScheduledEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase.GetScheduledEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase.ListScheduledEventsUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase.ScheduleEventUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
 * Demonstrates RabbitMQ scheduled/delayed events backed by a persisted {@code ScheduledEvent} row:
 * schedule one, read its lifecycle state, list them, and cancel a pending one (the broker still
 * delivers the in-flight message, but the listener skips a cancelled row).
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

    @Operation(summary = "Schedule an event to be delivered after delaySeconds")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ScheduledEventResponse>> schedule(
            @Valid @RequestBody ScheduleEventRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponseWrapper.ok(scheduleEventUseCase.execute(request)));
    }

    @Operation(summary = "Get a scheduled event's details and lifecycle status")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ScheduledEventResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getScheduledEventUseCase.execute(id)));
    }

    @Operation(summary = "List scheduled events (paged, optional status filter)")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<ScheduledEventResponse>>> list(
            @Valid ScheduledEventFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(listScheduledEventsUseCase.execute(filter)));
    }

    @Operation(summary = "Cancel a still-pending scheduled event")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ScheduledEventResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(cancelScheduledEventUseCase.execute(id)));
    }
}
