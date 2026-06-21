package com.gucardev.springreactboilerplate.features.news.service.usecase;

import com.gucardev.springreactboilerplate.features.news.mapper.NewsMapper;
import com.gucardev.springreactboilerplate.features.news.model.dto.NewsResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetNewsByIdUseCase {

    private final NewsFinder finder;
    private final NewsMapper mapper;

    @Transactional(readOnly = true)
    public NewsResponseDto execute(UUID id) {
        return mapper.toDto(finder.findById(id));
    }
}
