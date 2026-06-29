package com.gucardev.springreactboilerplate.features.news.adapter.in.web;

import com.gucardev.springreactboilerplate.features.news.adapter.in.web.dto.CreateNewsRequest;
import com.gucardev.springreactboilerplate.features.news.adapter.in.web.dto.NewsFilterRequest;
import com.gucardev.springreactboilerplate.features.news.adapter.in.web.dto.NewsResponseDto;
import com.gucardev.springreactboilerplate.features.news.adapter.in.web.dto.UpdateNewsRequest;
import com.gucardev.springreactboilerplate.features.news.application.port.in.CreateNewsCommand;
import com.gucardev.springreactboilerplate.features.news.application.port.in.CreateNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.application.port.in.DeleteNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.application.port.in.GetAllNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.application.port.in.GetNewsByIdUseCase;
import com.gucardev.springreactboilerplate.features.news.application.port.in.UpdateNewsCommand;
import com.gucardev.springreactboilerplate.features.news.application.port.in.UpdateNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.application.port.out.NewsSearchCriteria;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving (web) adapter for news. The controller only talks to input ports and maps between web DTOs
 * and the domain model.
 */
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "Manage news entries within the active workspace (X-Workspace-Id).")
public class NewsController {

    private final CreateNewsUseCase createNewsUseCase;
    private final UpdateNewsUseCase updateNewsUseCase;
    private final DeleteNewsUseCase deleteNewsUseCase;
    private final GetNewsByIdUseCase getNewsByIdUseCase;
    private final GetAllNewsUseCase getAllNewsUseCase;
    private final NewsWebMapper newsWebMapper;

    @Operation(summary = "List news in the active workspace (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<List<NewsResponseDto>>> getAll(@Valid NewsFilterRequest filter) {
        NewsSearchCriteria criteria = new NewsSearchCriteria(
                null, filter.getTitle(), filter.getFeatured(), filter.getTag(),
                filter.getStartDate(), filter.getEndDate());
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                getAllNewsUseCase.getAll(criteria, filter.toPageable()).map(newsWebMapper::toResponse)));
    }

    @Operation(summary = "Get a news entry by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<NewsResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                newsWebMapper.toResponse(getNewsByIdUseCase.getById(id))));
    }

    @Operation(summary = "Create a news entry")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<NewsResponseDto>> create(
            @Valid @RequestBody CreateNewsRequest request) {
        NewsResponseDto response = newsWebMapper.toResponse(createNewsUseCase.create(
                new CreateNewsCommand(
                        request.title(),
                        request.content(),
                        request.featured(),
                        request.imageIds(),
                        request.featuredImageId(),
                        request.attachmentIds(),
                        request.tags())));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Update a news entry")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<NewsResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateNewsRequest request) {
        NewsResponseDto response = newsWebMapper.toResponse(updateNewsUseCase.update(
                new UpdateNewsCommand(
                        id,
                        request.title(),
                        request.content(),
                        request.featured(),
                        request.imageIds(),
                        request.featuredImageId(),
                        request.attachmentIds(),
                        request.tags())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Delete a news entry")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteNewsUseCase.delete(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "News deleted"));
    }
}
