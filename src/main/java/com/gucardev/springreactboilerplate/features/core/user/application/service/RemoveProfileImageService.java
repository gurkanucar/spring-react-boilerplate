package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.RemoveProfileImageUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.ProfileImageStoragePort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.SaveUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clears the current user's profile image and best-effort deletes the underlying file.
 */
@Service
@RequiredArgsConstructor
public class RemoveProfileImageService implements RemoveProfileImageUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final ProfileImageStoragePort profileImageStoragePort;
    private final UserImageEnricher imageEnricher;

    @Override
    @Transactional
    public User removeProfileImage(String email) {
        User user = loadUserPort.findByEmail(email)
                .orElseThrow(UserExceptionType.CURRENT_USER_NOT_FOUND::toException);
        UUID current = user.getProfileImageId();
        if (current != null) {
            user.setProfileImageId(null);
            saveUserPort.save(user);
            try {
                profileImageStoragePort.deleteFile(current);
            } catch (RuntimeException ignored) {
                // already gone — fine
            }
        }
        imageEnricher.enrich(user);
        return user;
    }
}
