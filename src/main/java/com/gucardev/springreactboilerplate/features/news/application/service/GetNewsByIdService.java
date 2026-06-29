package com.gucardev.springreactboilerplate.features.news.application.service;

import com.gucardev.springreactboilerplate.features.news.application.port.in.GetNewsByIdUseCase;
import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetNewsByIdService implements GetNewsByIdUseCase {

    private final NewsFinder finder;

    @Override
    @Transactional(readOnly = true)
    public News getById(UUID id) {
        return finder.findById(id);
    }
}
