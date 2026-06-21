package com.gucardev.springreactboilerplate.features.core.user.service.usecase;

import com.gucardev.springreactboilerplate.features.core.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.features.core.user.model.dto.UserResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {

    private final UserFinder finder;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponseDto execute(UUID id) {
        return userMapper.toDto(finder.findById(id));
    }
}
