package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Output port: persist a user (insert or update) and return the stored state, including any
 * generated id and audit metadata.
 */
public interface SaveUserPort {

    User save(User user);
}
