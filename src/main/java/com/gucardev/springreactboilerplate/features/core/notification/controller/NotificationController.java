package com.gucardev.springreactboilerplate.features.core.notification.controller;

import com.gucardev.springreactboilerplate.features.core.notification.model.dto.NotificationDto;
import com.gucardev.springreactboilerplate.features.core.notification.model.request.NotificationFilterRequest;
import com.gucardev.springreactboilerplate.features.core.notification.service.usecase.GetMyNotificationsUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.service.usecase.GetUnreadCountUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.service.usecase.MarkAllNotificationsReadUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.service.usecase.MarkNotificationReadUseCase;
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

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "The current user's in-app notification feed for the active workspace.")
public class NotificationController {

    private final GetMyNotificationsUseCase getMyNotificationsUseCase;
    private final GetUnreadCountUseCase getUnreadCountUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final MarkAllNotificationsReadUseCase markAllNotificationsReadUseCase;

    @Operation(summary = "List my notifications in the active workspace (paged)")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<NotificationDto>>> getMine(
            @Valid NotificationFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getMyNotificationsUseCase.execute(filter)));
    }

    @Operation(summary = "Count my unread notifications")
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Map<String, Long>>> unreadCount() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(Map.of("count", getUnreadCountUseCase.execute())));
    }

    @Operation(summary = "Mark a notification read")
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<NotificationDto>> markRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(markNotificationReadUseCase.execute(id)));
    }

    @Operation(summary = "Mark all my notifications read")
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Map<String, Integer>>> markAllRead() {
        return ResponseEntity.ok(
                ApiResponseWrapper.ok(Map.of("updated", markAllNotificationsReadUseCase.execute())));
    }
}
