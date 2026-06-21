package com.gucardev.springreactboilerplate.features.core.notification.service.usecase;

import com.gucardev.springreactboilerplate.features.core.notification.repository.NotificationRepository;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUnreadCountUseCase {

    private final NotificationRepository repository;

    @Transactional(readOnly = true)
    public long execute() {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        UUID userId = SecurityUtils.requireCurrentUserId();
        return repository.countByWorkspaceIdAndRecipientIdAndReadFalse(workspaceId, userId);
    }
}
