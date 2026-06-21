package com.gucardev.springreactboilerplate.features.news.service.usecase;

import com.gucardev.springreactboilerplate.features.news.mapper.NewsMapper;
import com.gucardev.springreactboilerplate.features.news.model.dto.NewsResponseDto;
import com.gucardev.springreactboilerplate.features.news.model.request.NewsFilterRequest;
import com.gucardev.springreactboilerplate.features.news.repository.NewsRepository;
import com.gucardev.springreactboilerplate.features.news.repository.specification.NewsSpecification;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllNewsUseCase {

    private final NewsRepository repository;
    private final NewsMapper mapper;

    @Transactional(readOnly = true)
    public Page<NewsResponseDto> execute(NewsFilterRequest filter) {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        return repository.findAll(NewsSpecification.build(filter, workspaceId), filter.toPageable())
                .map(mapper::toDto);
    }
}
