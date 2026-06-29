package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Input port: clear the current user's profile image and return the updated (enriched) domain user.
 * {@code email} identifies the authenticated user (resolved by the web adapter).
 */
public interface RemoveProfileImageUseCase {

    User removeProfileImage(String email);
}
