package com.gucardev.springreactboilerplate.infra.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gucardev.springreactboilerplate.BaseMockMvcTest;
import com.gucardev.springreactboilerplate.domain.example.model.request.ExampleFilterRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
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
class GlobalExceptionHandlerIT extends BaseMockMvcTest {

    @Test
    void businessException_rendersEnvelope_withResolvedI18nMessage() throws Exception {
        mockMvc.perform(get("/public/__errtest/notfound").header("Accept-Language", "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.businessErrorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User with id 5 was not found."));
    }

    @Test
    void businessException_resolvesTurkishByDefaultLocale() throws Exception {
        mockMvc.perform(get("/public/__errtest/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("5 ID'li User bulunamadı."));
    }

    @Test
    void unexpectedRuntime_returns500_genericMessage_noLeak() throws Exception {
        mockMvc.perform(get("/public/__errtest/runtime").header("Accept-Language", "en"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An unexpected error occurred. Please try again later.")))
                // the raw exception text must NOT leak to the client
                .andExpect(content().string(not(containsString("super secret internal detail"))));
    }

    @Test
    void validationMessage_resolvesFromMessageBundle() throws Exception {
        // The @Pattern message {sort.direction.pattern.exception} must resolve from
        // messages.properties (validator wired to the app MessageSource), not render literally.
        mockMvc.perform(get("/public/__errtest/filter").param("sortDir", "bad").header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.sortDir").value("Sort direction must be 'asc' or 'desc'."));
    }

    @Test
    void validation_returns400_withFieldErrors() throws Exception {
        mockMvc.perform(post("/public/__errtest/validate")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.businessErrorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @RestController
    @RequestMapping("/public/__errtest")
    static class TestController {

        @GetMapping("/notfound")
        String notFound() {
            throw CommonExceptionType.NOT_FOUND.toException("User", 5);
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
