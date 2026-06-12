package com.gucardev.springreactboilerplate.domain.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gucardev.springreactboilerplate.domain.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.domain.example.model.request.CreateExampleRequest;
import com.gucardev.springreactboilerplate.domain.example.service.flow.DuplicateExampleFlow;
import com.gucardev.springreactboilerplate.domain.example.service.usecase.ActivateExampleUseCase;
import com.gucardev.springreactboilerplate.domain.example.service.usecase.CreateExampleUseCase;
import com.gucardev.springreactboilerplate.infra.exception.model.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifies the composition patterns at the component level (no HTTP/auth): a use case
 * orchestrating a domain service ({@link ActivateExampleUseCase} + the activation rule), and
 * a flow composing two use cases ({@link DuplicateExampleFlow}). Runs in the test profile's
 * H2 via {@code src/test/resources/application.properties}; @Transactional rolls back per test.
 */
@SpringBootTest
@Transactional
class ExampleCompositionIT {

    @Autowired
    private CreateExampleUseCase createExampleUseCase;
    @Autowired
    private ActivateExampleUseCase activateExampleUseCase;
    @Autowired
    private DuplicateExampleFlow duplicateExampleFlow;

    @Test
    void activate_setsActive_andEnforcesTheDomainRule() {
        ExampleResponseDto created =
                createExampleUseCase.execute(new CreateExampleRequest("demo", "desc", false));

        ExampleResponseDto activated = activateExampleUseCase.execute(created.getId());
        assertThat(activated.getActive()).isTrue();

        // The domain service rule: activating an already-active example is rejected.
        assertThatThrownBy(() -> activateExampleUseCase.execute(created.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getCode())
                .isEqualTo("EXAMPLE_ALREADY_ACTIVE");
    }

    @Test
    void duplicateFlow_copiesSourceIntoNewRecord() {
        ExampleResponseDto source =
                createExampleUseCase.execute(new CreateExampleRequest("orig", "the desc", true));

        ExampleResponseDto copy = duplicateExampleFlow.execute(source.getId());

        assertThat(copy.getId()).isNotEqualTo(source.getId());
        assertThat(copy.getName()).isEqualTo("orig (copy)");
        assertThat(copy.getDescription()).isEqualTo("the desc");
        assertThat(copy.getActive()).isTrue();
    }
}
