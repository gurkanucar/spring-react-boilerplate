package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.port.in.GetUserByIdUseCase;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserFinder finder;
    private final UserImageEnricher imageEnricher;

    @Override
    @Transactional(readOnly = true)
    public User getById(UUID id) {
        User user = finder.findById(id);
        imageEnricher.enrich(user);
        return user;
    }
}
