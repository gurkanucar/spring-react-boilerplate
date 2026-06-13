package com.gucardev.springreactboilerplate.domain.organization.service.usecase;

import com.gucardev.springreactboilerplate.domain.organization.entity.Organization;
import com.gucardev.springreactboilerplate.domain.organization.exception.OrganizationExceptionType;
import com.gucardev.springreactboilerplate.domain.organization.repository.OrganizationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationFinder {

    private final OrganizationRepository repository;

    public Organization findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> OrganizationExceptionType.NOT_FOUND.toException(id));
    }
}
