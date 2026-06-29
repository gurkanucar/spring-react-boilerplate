package com.gucardev.springreactboilerplate.features.news.application.service;

import com.gucardev.springreactboilerplate.features.news.application.exception.NewsExceptionType;
import com.gucardev.springreactboilerplate.features.news.application.port.in.UpdateNewsCommand;
import com.gucardev.springreactboilerplate.features.news.application.port.in.UpdateNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.application.port.out.SaveNewsPort;
import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import java.util.ArrayList;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateNewsService implements UpdateNewsUseCase {

    private final NewsFinder finder;
    private final SaveNewsPort saveNewsPort;

    @Override
    @Transactional
    public News update(UpdateNewsCommand command) {
        News news = finder.findById(command.id());
        // Null fields are left unchanged (mirrors the previous null-ignore mapping). The slug is not
        // regenerated on update so existing links stay valid.
        if (command.title() != null) {
            news.setTitle(command.title());
        }
        if (command.content() != null) {
            news.setContent(command.content());
        }
        if (command.featured() != null) {
            news.setFeatured(command.featured());
        }
        // Collections and the featured image are applied explicitly so that a provided value replaces
        // (rather than merges into) the existing one; a null leaves it untouched.
        if (command.imageIds() != null) {
            news.setImageIds(new ArrayList<>(command.imageIds()));
        }
        if (command.attachmentIds() != null) {
            news.setAttachmentIds(new ArrayList<>(command.attachmentIds()));
        }
        if (command.tags() != null) {
            news.setTags(new HashSet<>(command.tags()));
        }
        if (command.featuredImageId() != null) {
            news.setFeaturedImageId(command.featuredImageId());
        }
        if (!news.isFeaturedImageConsistent()) {
            throw NewsExceptionType.FEATURED_IMAGE_NOT_IN_IMAGES.toException();
        }
        return saveNewsPort.save(news);
    }
}
