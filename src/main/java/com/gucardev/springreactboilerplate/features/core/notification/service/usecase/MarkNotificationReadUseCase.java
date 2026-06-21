package com.gucardev.springreactboilerplate.features.core.notification.service.usecase;

import com.gucardev.springreactboilerplate.features.core.notification.entity.Notification;
import com.gucardev.springreactboilerplate.features.core.notification.mapper.NotificationMapper;
import com.gucardev.springreactboilerplate.features.core.notification.model.dto.NotificationDto;
import com.gucardev.springreactboilerplate.features.core.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkNotificationReadUseCase {

    private final NotificationFinder finder;
    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Transactional
    public NotificationDto execute(UUID id) {
        Notification notification = finder.findOwn(id);
        if (!Boolean.TRUE.equals(notification.getRead())) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            repository.save(notification);
        }
        return mapper.toDto(notification);
    }
}
