package com.gucardev.springreactboilerplate.features.example.adapter.in.web;

import com.gucardev.springreactboilerplate.features.example.adapter.in.web.dto.CreateExampleRequest;
import com.gucardev.springreactboilerplate.features.example.adapter.in.web.dto.ExampleFilterRequest;
import com.gucardev.springreactboilerplate.features.example.adapter.in.web.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.features.example.adapter.in.web.dto.UpdateExampleRequest;
import com.gucardev.springreactboilerplate.features.example.application.port.in.ActivateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.in.CreateExampleCommand;
import com.gucardev.springreactboilerplate.features.example.application.port.in.CreateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.in.DeleteExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.in.ExampleSearchCriteria;
import com.gucardev.springreactboilerplate.features.example.application.port.in.GetAllExamplesUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.in.GetExampleByIdUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.in.UpdateExampleCommand;
import com.gucardev.springreactboilerplate.features.example.application.port.in.UpdateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.service.DuplicateExampleFlow;
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
 * Driving (web) adapter: one injected input port per operation, responses wrapped in the standard
 * {@link ApiResponseWrapper}. The controller only talks to input ports (and the duplicate flow) and
 * maps between web DTOs and the domain model.
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
    private final ExampleWebMapper exampleWebMapper;

    @Operation(summary = "List examples (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponseWrapper<List<ExampleResponseDto>>> getAll(
            @Valid ExampleFilterRequest filter) {
        ExampleSearchCriteria criteria =
                new ExampleSearchCriteria(filter.getName(), filter.getStartDate(), filter.getEndDate());
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                getAllExamplesUseCase.execute(criteria, filter.toPageable())
                        .map(exampleWebMapper::toResponse)));
    }

    @Operation(summary = "Get an example by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                exampleWebMapper.toResponse(getExampleByIdUseCase.execute(id))));
    }

    @Operation(summary = "Create an example (admin)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> create(
            @Valid @RequestBody CreateExampleRequest request) {
        ExampleResponseDto response = exampleWebMapper.toResponse(createExampleUseCase.execute(
                new CreateExampleCommand(request.name(), request.description(), request.active())));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Update an example (admin)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateExampleRequest request) {
        ExampleResponseDto response = exampleWebMapper.toResponse(updateExampleUseCase.execute(id,
                new UpdateExampleCommand(request.name(), request.description(), request.active())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
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
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                exampleWebMapper.toResponse(activateExampleUseCase.execute(id))));
    }

    @Operation(summary = "Duplicate an example into a new record (admin) — orchestration flow")
    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<ExampleResponseDto>> duplicate(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(
                        exampleWebMapper.toResponse(duplicateExampleFlow.execute(id))));
    }
}
