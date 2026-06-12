package com.gucardev.springreactboilerplate.domain.example.model.request;

import com.gucardev.springreactboilerplate.domain.shared.dto.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Adds example-specific filters on top of the shared paging/sorting/date filters.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ExampleFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by name (contains, case-insensitive)", example = "demo")
    @Size(max = 150)
    private String name;
}
