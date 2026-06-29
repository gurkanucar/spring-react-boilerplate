package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import org.springframework.data.domain.Page;

/**
 * Input port: list users (paged, sorted and filtered), returning domain models enriched with
 * resolved profile-image URLs.
 */
public interface GetAllUsersUseCase {

    Page<User> getAll(UserSearchQuery query);
}
