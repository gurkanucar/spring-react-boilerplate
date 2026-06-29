package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Output port: search users with dynamic criteria. The persistence adapter translates the
 * {@link UserSearchCriteria} into a query (Specification) and returns domain models.
 */
public interface SearchUsersPort {

    Page<User> search(UserSearchCriteria criteria, Pageable pageable);
}
