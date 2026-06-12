package com.gucardev.springreactboilerplate.infra.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end check that {@link GlobalExceptionHandler} renders the {@link
 * com.gucardev.springreactboilerplate.infra.exception.model.ApiError} envelope and that
 * i18n messages resolve from the bundles. Security filters are disabled so requests reach
 * the controller advice (the live app returns a filter-level 401 before then).
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("dev")
@Import(GlobalExceptionHandlerIT.TestController.class)
class GlobalExceptionHandlerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void businessException_rendersEnvelope_withResolvedI18nMessage() throws Exception {
        mockMvc.perform(get("/__test/notfound").header("Accept-Language", "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.businessErrorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User with id 5 was not found."))
                .andExpect(jsonPath("$.traceId").doesNotExist()); // no trace context in this test
    }

    @Test
    void businessException_resolvesTurkishByDefaultLocale() throws Exception {
        mockMvc.perform(get("/__test/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("5 kimlikli User bulunamadı."));
    }

    @Test
    void unexpectedRuntime_returns500_genericMessage_noLeak() throws Exception {
        mockMvc.perform(get("/__test/runtime").header("Accept-Language", "en"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."))
                // the raw exception text must NOT leak to the client
                .andExpect(content().string(not(containsString("super secret internal detail"))));
    }

    @Test
    void validation_returns400_withFieldErrors() throws Exception {
        mockMvc.perform(post("/__test/validate")
                        .header("Accept-Language", "en")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.businessErrorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @RestController
    @RequestMapping("/__test")
    static class TestController {

        @GetMapping("/notfound")
        String notFound() {
            throw ExceptionUtil.notFound("User", 5);
        }

        @GetMapping("/runtime")
        String runtime() {
            throw new IllegalStateException("super secret internal detail");
        }

        @PostMapping("/validate")
        String validate(@RequestBody @Valid Payload payload) {
            return "ok";
        }

        record Payload(@NotBlank String name) {
        }
    }
}
