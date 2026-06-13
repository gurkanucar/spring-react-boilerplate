package com.gucardev.springreactboilerplate.domain.workspace.model.request;

import com.gucardev.springreactboilerplate.domain.shared.dto.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WorkspaceFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by name (contains, case-insensitive)", example = "downtown")
    @Size(max = 200)
    private String name;

    @Schema(description = "Filter by active flag", example = "true")
    private Boolean isActive;

    @Schema(description = "Filter by organization (super-admin only; ignored for org users)")
    private UUID organizationId;
}
