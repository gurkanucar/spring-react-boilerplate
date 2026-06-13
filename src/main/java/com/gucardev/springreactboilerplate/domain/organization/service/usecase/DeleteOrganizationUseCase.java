package com.gucardev.springreactboilerplate.domain.organization.service.usecase;

import com.gucardev.springreactboilerplate.domain.organization.repository.OrganizationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteOrganizationUseCase {

    private final OrganizationFinder finder;
    private final OrganizationRepository repository;

    @Transactional
    public void execute(UUID id) {
        repository.delete(finder.findById(id));
    }
}
