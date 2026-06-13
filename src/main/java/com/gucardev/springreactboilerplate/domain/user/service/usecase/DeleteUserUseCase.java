package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {

    private final UserFinder finder;
    private final UserRepository repository;

    @Transactional
    public void execute(UUID id) {
        repository.delete(finder.findById(id));
    }
}
