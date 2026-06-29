package com.gucardev.springreactboilerplate.features.news.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.news.application.port.out.DeleteNewsPort;
import com.gucardev.springreactboilerplate.features.news.application.port.out.LoadNewsPort;
import com.gucardev.springreactboilerplate.features.news.application.port.out.NewsSearchCriteria;
import com.gucardev.springreactboilerplate.features.news.application.port.out.SaveNewsPort;
import com.gucardev.springreactboilerplate.features.news.application.port.out.SearchNewsPort;
import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the news load/save/delete/search output ports with Spring Data JPA. Maps
 * domain ⇄ entity at the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class NewsPersistenceAdapter implements LoadNewsPort, SaveNewsPort, DeleteNewsPort, SearchNewsPort {

    private final NewsJpaRepository repository;
    private final NewsPersistenceMapper mapper;

    @Override
    public Optional<News> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByWorkspaceIdAndSlug(UUID workspaceId, String slug) {
        return repository.existsByWorkspaceIdAndSlug(workspaceId, slug);
    }

    @Override
    public News save(News news) {
        return mapper.toDomain(repository.save(mapper.toEntity(news)));
    }

    @Override
    public void delete(News news) {
        repository.delete(mapper.toEntity(news));
    }

    @Override
    public Page<News> search(NewsSearchCriteria criteria, Pageable pageable) {
        return repository.findAll(NewsSpecification.build(criteria), pageable)
                .map(mapper::toDomain);
    }
}
