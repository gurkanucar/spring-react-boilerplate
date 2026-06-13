package com.gucardev.springreactboilerplate.domain.role.model.request;

import com.gucardev.springreactboilerplate.domain.shared.dto.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Adds role-specific filters on top of the shared paging/sorting/date filters.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RoleFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by name (contains, case-insensitive)", example = "admin")
    @Size(max = 50)
    private String name;
}
