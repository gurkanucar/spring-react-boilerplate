package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.port.in.GetAllUsersUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.UserSearchQuery;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.SearchUsersPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.UserSearchCriteria;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllUsersService implements GetAllUsersUseCase {

    private final SearchUsersPort searchUsersPort;
    private final UserImageEnricher imageEnricher;

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAll(UserSearchQuery query) {
        UserSearchCriteria criteria = new UserSearchCriteria(
                query.email(), query.name(), query.activated(), query.isActive(),
                query.startDate(), query.endDate());
        Page<User> users = searchUsersPort.search(criteria, query.pageable());
        // Resolve every profile image on the page in one batch, then attach the URLs.
        imageEnricher.enrich(users.getContent());
        return users;
    }
}
