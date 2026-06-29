package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Output port: delete a user.
 */
public interface DeleteUserPort {

    void delete(User user);
}
