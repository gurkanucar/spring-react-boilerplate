package com.gucardev.springreactboilerplate.features.core.user.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.user.application.port.out.DeleteUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.SaveUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.SearchUsersPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.UserSearchCriteria;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the user load/save/delete/search output ports with Spring Data JPA. Maps
 * domain ⇄ entity at the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter
        implements LoadUserPort, SaveUserPort, DeleteUserPort, SearchUsersPort {

    private final UserJpaRepository repository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Override
    public void delete(User user) {
        repository.delete(mapper.toEntity(user));
    }

    @Override
    public Page<User> search(UserSearchCriteria criteria, Pageable pageable) {
        return repository.findAll(UserSpecification.build(criteria), pageable).map(mapper::toDomain);
    }
}
