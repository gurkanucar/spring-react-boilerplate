package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.UUID;

/**
 * Input port: update a user (admin), returning the updated domain model.
 */
public interface UpdateUserUseCase {

    User update(UUID id, UpdateUserCommand command);
}
