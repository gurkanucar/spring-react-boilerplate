package com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.role.application.port.out.DeleteRolePort;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.LoadRolePort;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.RoleSearchCriteria;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.SaveRolePort;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.SearchRolesPort;
import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the role load/save/delete/search output ports with Spring Data JPA. Maps
 * domain ⇄ entity at the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter
        implements LoadRolePort, SaveRolePort, DeleteRolePort, SearchRolesPort {

    private final RoleJpaRepository repository;
    private final RolePersistenceMapper mapper;

    @Override
    public Optional<Role> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Role save(Role role) {
        return mapper.toDomain(repository.save(mapper.toEntity(role)));
    }

    @Override
    public void delete(Role role) {
        repository.delete(mapper.toEntity(role));
    }

    @Override
    public Page<Role> search(RoleSearchCriteria criteria, Pageable pageable) {
        return repository.findAll(RoleSpecification.build(criteria), pageable).map(mapper::toDomain);
    }
}
