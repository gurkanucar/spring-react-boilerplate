package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.ProfileImageStorageType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Driving-side command to set a user's profile image. {@code email} identifies the target (the
 * authenticated user, resolved by the web adapter); {@code storageType} selects the backend, or
 * {@code null} for the configured default.
 */
public record SetProfileImageCommand(
        String email,
        MultipartFile file,
        ProfileImageStorageType storageType
) {
}
