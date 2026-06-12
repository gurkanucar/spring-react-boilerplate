package com.gucardev.springreactboilerplate.domain.example.service.flow;

import com.gucardev.springreactboilerplate.domain.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.domain.example.model.request.CreateExampleRequest;
import com.gucardev.springreactboilerplate.domain.example.service.usecase.CreateExampleUseCase;
import com.gucardev.springreactboilerplate.domain.example.service.usecase.GetExampleByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * A <strong>flow</strong>: the orchestration layer that composes whole use cases for a
 * higher-level scenario (here: read a source example, then create a copy of it). This is the
 * <em>only</em> layer allowed to call multiple use cases — use cases never call each other.
 *
 * <p>Deliberately not {@code @Transactional}: a flow sequences independently-committing use
 * cases (each owns its own transaction). If a scenario needs all steps to commit atomically,
 * it isn't a flow — make it a single {@code @Transactional} use case over collaborators.
 *
 * <p>Dependency direction: controller → flow → use cases → collaborators (finder) → repository.
 */
@Service
@RequiredArgsConstructor
public class DuplicateExampleFlow {

    private final GetExampleByIdUseCase getExampleById;
    private final CreateExampleUseCase createExample;

    public ExampleResponseDto execute(Long id) {
        ExampleResponseDto source = getExampleById.execute(id);
        CreateExampleRequest copy = new CreateExampleRequest(
                source.getName() + " (copy)",
                source.getDescription(),
                source.getActive());
        return createExample.execute(copy);
    }
}
