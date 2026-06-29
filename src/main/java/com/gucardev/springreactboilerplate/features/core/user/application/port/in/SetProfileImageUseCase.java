package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Input port: set the current user's profile image and return the updated (enriched) domain user.
 */
public interface SetProfileImageUseCase {

    User setProfileImage(SetProfileImageCommand command);
}
