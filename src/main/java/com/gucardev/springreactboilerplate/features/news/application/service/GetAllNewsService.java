package com.gucardev.springreactboilerplate.features.news.application.service;

import com.gucardev.springreactboilerplate.features.news.application.port.in.GetAllNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.application.port.out.NewsSearchCriteria;
import com.gucardev.springreactboilerplate.features.news.application.port.out.SearchNewsPort;
import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllNewsService implements GetAllNewsUseCase {

    private final SearchNewsPort searchNewsPort;

    @Override
    @Transactional(readOnly = true)
    public Page<News> getAll(NewsSearchCriteria criteria, Pageable pageable) {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        return searchNewsPort.search(criteria.withWorkspaceId(workspaceId), pageable);
    }
}
