package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.UUID;

/**
 * Input port: get a single user by id (fetch-or-404), enriched with resolved profile-image URLs.
 */
public interface GetUserByIdUseCase {

    User getById(UUID id);
}
