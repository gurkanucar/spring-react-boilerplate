package com.gucardev.springreactboilerplate.features.core.role.model.dto;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Role resource with its audit metadata.")
public class RoleResponseDto extends BaseDto {

    @Schema(description = "Identifier", example = "1")
    private Long id;

    @Schema(description = "Role name (no ROLE_ prefix)", example = "ADMIN")
    private String name;

    @Schema(description = "Human-readable label", example = "Administrator")
    private String displayName;

    @Schema(description = "What the role grants", example = "Full system access")
    private String description;
}
