package com.gucardev.springreactboilerplate.features.news.service.usecase;

import com.gucardev.springreactboilerplate.features.news.entity.News;
import com.gucardev.springreactboilerplate.features.news.exception.NewsExceptionType;
import com.gucardev.springreactboilerplate.features.news.mapper.NewsMapper;
import com.gucardev.springreactboilerplate.features.news.model.dto.NewsResponseDto;
import com.gucardev.springreactboilerplate.features.news.model.request.UpdateNewsRequest;
import com.gucardev.springreactboilerplate.features.news.repository.NewsRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateNewsUseCase {

    private final NewsFinder finder;
    private final NewsRepository repository;
    private final NewsMapper mapper;

    @Transactional
    public NewsResponseDto execute(UUID id, UpdateNewsRequest request) {
        News news = finder.findById(id);
        mapper.updateEntity(request, news);
        if (request.imageIds() != null) {
            news.setImageIds(new ArrayList<>(request.imageIds()));
        }
        if (request.attachmentIds() != null) {
            news.setAttachmentIds(new ArrayList<>(request.attachmentIds()));
        }
        if (request.tags() != null) {
            news.setTags(new HashSet<>(request.tags()));
        }
        if (request.featuredImageId() != null) {
            news.setFeaturedImageId(request.featuredImageId());
        }
        if (!news.isFeaturedImageConsistent()) {
            throw NewsExceptionType.FEATURED_IMAGE_NOT_IN_IMAGES.toException();
        }
        return mapper.toDto(repository.save(news));
    }
}
