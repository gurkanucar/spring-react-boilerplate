package com.gucardev.springreactboilerplate.features.news.service.usecase;

import com.gucardev.springreactboilerplate.features.news.repository.NewsRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteNewsUseCase {

    private final NewsFinder finder;
    private final NewsRepository repository;

    @Transactional
    public void execute(UUID id) {
        repository.delete(finder.findById(id));
    }
}
