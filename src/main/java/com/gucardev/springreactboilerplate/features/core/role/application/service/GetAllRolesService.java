package com.gucardev.springreactboilerplate.features.core.role.application.service;

import com.gucardev.springreactboilerplate.features.core.role.application.port.in.GetAllRolesUseCase;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.RoleSearchQuery;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.RoleSearchCriteria;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.SearchRolesPort;
import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllRolesService implements GetAllRolesUseCase {

    private final SearchRolesPort searchRolesPort;

    @Override
    @Transactional(readOnly = true)
    public Page<Role> getAll(RoleSearchQuery query) {
        RoleSearchCriteria criteria =
                new RoleSearchCriteria(query.name(), query.startDate(), query.endDate());
        return searchRolesPort.search(criteria, query.pageable());
    }
}
