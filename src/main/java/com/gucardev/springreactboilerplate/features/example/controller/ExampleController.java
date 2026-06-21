package com.gucardev.springreactboilerplate.features.example.controller;

import com.gucardev.springreactboilerplate.features.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.features.example.model.request.CreateExampleRequest;
import com.gucardev.springreactboilerplate.features.example.model.request.ExampleFilterRequest;
import com.gucardev.springreactboilerplate.features.example.model.request.UpdateExampleRequest;
import com.gucardev.springreactboilerplate.features.example.service.flow.DuplicateExampleFlow;
import com.gucardev.springreactboilerplate.features.example.service.usecase.ActivateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.service.usecase.CreateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.service.usecase.DeleteExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.service.usecase.GetAllExamplesUseCase;
import com.gucardev.springreactboilerplate.features.example.service.usecase.GetExampleByIdUseCase;
import com.gucardev.springreactboilerplate.features.example.service.usecase.UpdateExampleUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
 * Reference REST controller: one injected use case per operation, responses wrapped in the
 * standard {@link ApiResponseWrapper}.
 */
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
@Tag(name = "Example", description = "Reference CRUD endpoints demonstrating the domain layout.")
public class ExampleController {

    private final CreateExampleUseCase createExampleUseCase;
    private final UpdateExampleUseCase updateExampleUseCase;
    private final DeleteExampleUseCase deleteExampleUseCase;
    private final GetExampleByIdUseCase getExampleByIdUseCase;
    private final GetAllExamplesUseCase getAllExamplesUseCase;
    private final ActivateExampleUseCase activateExampleUseCase;
    private final DuplicateExampleFlow duplicateExampleFlow;

    @Operation(summary = "List examples (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponseWrapper<List<ExampleResponseDto>>> getAll(
            @Valid ExampleFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getAllExamplesUseCase.execute(filter)));
    }

    @Operation(summary = "Get an example by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getExampleByIdUseCase.execute(id)));
    }

    @Operation(summary = "Create an example (admin)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> create(
            @Valid @RequestBody CreateExampleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(createExampleUseCase.execute(request)));
    }

    @Operation(summary = "Update an example (admin)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateExampleRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(updateExampleUseCase.execute(id, request)));
    }

    @Operation(summary = "Delete an example (admin)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable Long id) {
        deleteExampleUseCase.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Example deleted"));
    }

    @Operation(summary = "Activate an example (admin)")
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> activate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(activateExampleUseCase.execute(id)));
    }

    @Operation(summary = "Duplicate an example into a new record (admin) — orchestration flow")
    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> duplicate(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(duplicateExampleFlow.execute(id)));
    }
}
