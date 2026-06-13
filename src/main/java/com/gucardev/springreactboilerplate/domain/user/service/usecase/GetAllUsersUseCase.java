package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.model.request.UserFilterRequest;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import com.gucardev.springreactboilerplate.domain.user.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public Page<UserResponseDto> execute(UserFilterRequest filter) {
        return repository.findAll(UserSpecification.build(filter), filter.toPageable())
                .map(mapper::toDto);
    }
}
