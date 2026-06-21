package com.gucardev.springreactboilerplate.features.news.controller;

import com.gucardev.springreactboilerplate.features.news.model.dto.NewsResponseDto;
import com.gucardev.springreactboilerplate.features.news.model.request.CreateNewsRequest;
import com.gucardev.springreactboilerplate.features.news.model.request.NewsFilterRequest;
import com.gucardev.springreactboilerplate.features.news.model.request.UpdateNewsRequest;
import com.gucardev.springreactboilerplate.features.news.service.usecase.CreateNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.service.usecase.DeleteNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.service.usecase.GetAllNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.service.usecase.GetNewsByIdUseCase;
import com.gucardev.springreactboilerplate.features.news.service.usecase.UpdateNewsUseCase;
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

    @Operation(summary = "List news in the active workspace (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<List<NewsResponseDto>>> getAll(@Valid NewsFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getAllNewsUseCase.execute(filter)));
    }

    @Operation(summary = "Get a news entry by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<NewsResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getNewsByIdUseCase.execute(id)));
    }

    @Operation(summary = "Create a news entry")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<NewsResponseDto>> create(
            @Valid @RequestBody CreateNewsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(createNewsUseCase.execute(request)));
    }

    @Operation(summary = "Update a news entry")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<NewsResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateNewsRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(updateNewsUseCase.execute(id, request)));
    }

    @Operation(summary = "Delete a news entry")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','WORKSPACE_USER')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteNewsUseCase.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "News deleted"));
    }
}
