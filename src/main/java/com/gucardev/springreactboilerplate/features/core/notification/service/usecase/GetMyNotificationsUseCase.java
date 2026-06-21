package com.gucardev.springreactboilerplate.features.core.notification.service.usecase;

import com.gucardev.springreactboilerplate.features.core.notification.mapper.NotificationMapper;
import com.gucardev.springreactboilerplate.features.core.notification.model.dto.NotificationDto;
import com.gucardev.springreactboilerplate.features.core.notification.model.request.NotificationFilterRequest;
import com.gucardev.springreactboilerplate.features.core.notification.repository.NotificationRepository;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyNotificationsUseCase {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Transactional(readOnly = true)
    public Page<NotificationDto> execute(NotificationFilterRequest filter) {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        UUID userId = SecurityUtils.requireCurrentUserId();
        Page<com.gucardev.springreactboilerplate.features.core.notification.entity.Notification> page =
                Boolean.TRUE.equals(filter.getUnreadOnly())
                        ? repository.findByWorkspaceIdAndRecipientIdAndReadFalse(workspaceId, userId, filter.toPageable())
                        : repository.findByWorkspaceIdAndRecipientId(workspaceId, userId, filter.toPageable());
        return page.map(mapper::toDto);
    }
}
