package com.gucardev.springreactboilerplate.features.core.notification.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.notification.adapter.in.web.dto.NotificationFilterRequest;
import com.gucardev.springreactboilerplate.features.core.notification.adapter.in.web.dto.NotificationResponse;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.GetMyNotificationsUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.GetUnreadCountUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.MarkAllNotificationsReadUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.MarkNotificationReadUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.NotificationQuery;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving (web) adapter for the current user's in-app notification feed. The controller only talks to
 * input ports and maps between web DTOs and the domain model.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "The current user's in-app notification feed for the active workspace.")
public class NotificationController {

    private final GetMyNotificationsUseCase getMyNotificationsUseCase;
    private final GetUnreadCountUseCase getUnreadCountUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final MarkAllNotificationsReadUseCase markAllNotificationsReadUseCase;
    private final NotificationWebMapper notificationWebMapper;

    @Operation(summary = "List my notifications in the active workspace (paged)")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<NotificationResponse>>> getMine(
            @Valid NotificationFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                getMyNotificationsUseCase
                        .getMine(new NotificationQuery(filter.toPageable(), filter.getUnreadOnly()))
                        .map(notificationWebMapper::toResponse)));
    }

    @Operation(summary = "Count my unread notifications")
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Map<String, Long>>> unreadCount() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(Map.of("count", getUnreadCountUseCase.getUnreadCount())));
    }

    @Operation(summary = "Mark a notification read")
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<NotificationResponse>> markRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                notificationWebMapper.toResponse(markNotificationReadUseCase.markRead(id))));
    }

    @Operation(summary = "Mark all my notifications read")
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Map<String, Integer>>> markAllRead() {
        return ResponseEntity.ok(
                ApiResponseWrapper.ok(Map.of("updated", markAllNotificationsReadUseCase.markAllRead())));
    }
}
