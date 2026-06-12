package com.gucardev.springreactboilerplate.infra.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.example.model.request.ExampleFilterRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * End-to-end check that {@link GlobalExceptionHandler} renders the {@link
 * com.gucardev.springreactboilerplate.infra.exception.model.ApiError} envelope and that
 * i18n messages resolve from the bundles. The test endpoints live under a {@code /public/**}
 * path (permitted by {@code security.ignored-paths}) so the request reaches the controller
 * advice with security enabled.
 */
@Import(GlobalExceptionHandlerIT.TestController.class)
class GlobalExceptionHandlerIT extends BaseIntegrationTest {

    @Test
    void businessException_rendersEnvelope_withResolvedI18nMessage() {
        client.get().uri("/public/__errtest/notfound").header("Accept-Language", "en")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.businessErrorCode").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("User with id 5 was not found.");
    }

    @Test
    void businessException_resolvesTurkishByDefaultLocale() {
        client.get().uri("/public/__errtest/notfound")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("5 ID'li User bulunamadı.");
    }

    @Test
    void unexpectedRuntime_returns500_genericMessage_noLeak() {
        client.get().uri("/public/__errtest/runtime").header("Accept-Language", "en")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body).contains("An unexpected error occurred. Please try again later.");
                    // the raw exception text must NOT leak to the client
                    assertThat(body).doesNotContain("super secret internal detail");
                });
    }

    @Test
    void validationMessage_resolvesFromMessageBundle() {
        // The @Pattern message {sort.direction.pattern.exception} must resolve from
        // messages.properties (validator wired to the app MessageSource), not render literally.
        client.get().uri("/public/__errtest/filter?sortDir=bad").header("Accept-Language", "en")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.validationErrors.sortDir").isEqualTo("Sort direction must be 'asc' or 'desc'.");
    }

    @Test
    void validation_returns400_withFieldErrors() {
        client.post().uri("/public/__errtest/validate")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.businessErrorCode").isEqualTo("VALIDATION_FAILED")
                .jsonPath("$.validationErrors.name").exists();
    }

    @RestController
    @RequestMapping("/public/__errtest")
    static class TestController {

        @GetMapping("/notfound")
        String notFound() {
            throw ExceptionUtil.notFound("User", 5);
        }

        @GetMapping("/runtime")
        String runtime() {
            throw new IllegalStateException("super secret internal detail");
        }

        @GetMapping("/filter")
        String filter(@Valid ExampleFilterRequest filter) {
            return "ok";
        }

        @PostMapping("/validate")
        String validate(@RequestBody @Valid Payload payload) {
            return "ok";
        }

        record Payload(@NotBlank String name) {
        }
    }
}
