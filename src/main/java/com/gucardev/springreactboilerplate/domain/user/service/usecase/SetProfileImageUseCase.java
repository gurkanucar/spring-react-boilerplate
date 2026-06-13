package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.auth.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.domain.file.enums.StorageType;
import com.gucardev.springreactboilerplate.domain.file.model.dto.FileResponseDto;
import com.gucardev.springreactboilerplate.domain.file.service.usecase.DeleteFileUseCase;
import com.gucardev.springreactboilerplate.domain.file.service.usecase.UploadImageUseCase;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Sets the current user's profile image: uploads it through the image pipeline (optimized WebP +
 * thumbnail), points the user at the new file, and best-effort deletes the previous one. The user
 * just references the file by id — all storage/optimization lives in the file domain.
 */
@Service
@RequiredArgsConstructor
public class SetProfileImageUseCase {

    private final UserRepository userRepository;
    private final UploadImageUseCase uploadImageUseCase;
    private final DeleteFileUseCase deleteFileUseCase;
    private final UserMapper userMapper;

    /**
     * @param storageType backend to store the image in; {@code null} uses the configured default.
     */
    @Transactional
    public UserResponseDto execute(MultipartFile file, StorageType storageType) {
        User user = currentUser();
        UUID previous = user.getProfileImageId();

        FileResponseDto uploaded = uploadImageUseCase.execute(file, storageType);
        user.setProfileImageId(uploaded.getId());
        userRepository.save(user);

        if (previous != null) {
            deleteQuietly(previous);
        }
        return userMapper.toDto(user);
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(AuthExceptionType.USER_NOT_FOUND::toException);
    }

    private void deleteQuietly(UUID fileId) {
        try {
            deleteFileUseCase.execute(fileId);
        } catch (RuntimeException ignored) {
            // Old image already gone / unreachable — not worth failing the update over.
        }
    }
}
