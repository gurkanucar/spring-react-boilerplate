package com.gucardev.springreactboilerplate.features.core.user.service.usecase;

import com.gucardev.springreactboilerplate.features.core.auth.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.features.core.file.service.usecase.DeleteFileUseCase;
import com.gucardev.springreactboilerplate.features.core.user.entity.User;
import com.gucardev.springreactboilerplate.features.core.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.features.core.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.features.core.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clears the current user's profile image and best-effort deletes the underlying file.
 */
@Service
@RequiredArgsConstructor
public class RemoveProfileImageUseCase {

    private final UserRepository userRepository;
    private final DeleteFileUseCase deleteFileUseCase;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto execute() {
        User user = currentUser();
        UUID current = user.getProfileImageId();
        if (current != null) {
            user.setProfileImageId(null);
            userRepository.save(user);
            try {
                deleteFileUseCase.execute(current);
            } catch (RuntimeException ignored) {
                // already gone — fine
            }
        }
        return userMapper.toDto(user);
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(AuthExceptionType.USER_NOT_FOUND::toException);
    }
}
