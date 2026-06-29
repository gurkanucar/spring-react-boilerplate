package com.gucardev.springreactboilerplate.features.example.application.service;

import com.gucardev.springreactboilerplate.features.example.application.port.in.CreateExampleCommand;
import com.gucardev.springreactboilerplate.features.example.application.port.in.CreateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.in.GetExampleByIdUseCase;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
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
 * <p>Dependency direction: controller → flow → use cases → ports → adapters.
 */
@Service
@RequiredArgsConstructor
public class DuplicateExampleFlow {

    private final GetExampleByIdUseCase getExampleById;
    private final CreateExampleUseCase createExample;

    public Example execute(Long id) {
        Example source = getExampleById.execute(id);
        CreateExampleCommand copy = new CreateExampleCommand(
                source.getName() + " (copy)",
                source.getDescription(),
                source.getActive());
        return createExample.execute(copy);
    }
}
