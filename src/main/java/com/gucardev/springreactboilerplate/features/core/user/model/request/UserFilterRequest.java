package com.gucardev.springreactboilerplate.features.core.user.model.request;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Adds user-specific filters on top of the shared paging/sorting/date filters.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by email (contains, case-insensitive)", example = "mail.com")
    @Size(max = 255)
    private String email;

    @Schema(description = "Filter by first name (contains, case-insensitive)", example = "jane")
    @Size(max = 100)
    private String name;

    @Schema(description = "Filter by verified flag", example = "true")
    private Boolean activated;

    @Schema(description = "Filter by enabled flag", example = "true")
    private Boolean isActive;
}
