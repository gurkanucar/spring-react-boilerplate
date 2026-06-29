package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.SetProfileImageCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.SetProfileImageUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.ProfileImageStoragePort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.SaveUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sets the current user's profile image: uploads it through the image pipeline (optimized WebP +
 * thumbnail), points the user at the new file, and best-effort deletes the previous one. The user
 * just references the file by id — all storage/optimization lives behind the file port.
 */
@Service
@RequiredArgsConstructor
public class SetProfileImageService implements SetProfileImageUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final ProfileImageStoragePort profileImageStoragePort;
    private final UserImageEnricher imageEnricher;

    @Override
    @Transactional
    public User setProfileImage(SetProfileImageCommand command) {
        User user = loadUserPort.findByEmail(command.email())
                .orElseThrow(UserExceptionType.CURRENT_USER_NOT_FOUND::toException);
        UUID previous = user.getProfileImageId();

        UUID uploadedId = profileImageStoragePort.uploadImage(command.file(), command.storageType());
        user.setProfileImageId(uploadedId);
        User saved = saveUserPort.save(user);

        if (previous != null) {
            deleteQuietly(previous);
        }
        imageEnricher.enrich(saved);
        return saved;
    }

    private void deleteQuietly(UUID fileId) {
        try {
            profileImageStoragePort.deleteFile(fileId);
        } catch (RuntimeException ignored) {
            // Old image already gone / unreachable — not worth failing the update over.
        }
    }
}
