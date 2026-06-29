package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import java.util.UUID;

/**
 * Input port: delete a user (admin).
 */
public interface DeleteUserUseCase {

    void delete(UUID id);
}
