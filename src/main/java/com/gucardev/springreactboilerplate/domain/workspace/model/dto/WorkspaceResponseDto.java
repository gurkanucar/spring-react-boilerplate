package com.gucardev.springreactboilerplate.domain.workspace.model.dto;

import com.gucardev.springreactboilerplate.domain.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
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
@Schema(description = "Workspace with its audit metadata.")
public class WorkspaceResponseDto extends BaseDto {

    @Schema(description = "Identifier")
    private UUID id;

    @Schema(description = "Display name", example = "Downtown Branch")
    private String name;

    @Schema(description = "URL-safe unique slug", example = "downtown")
    private String slug;

    @Schema(description = "Free-text description")
    private String description;

    @Schema(description = "Phone number")
    private String phoneNumber;

    @Schema(description = "Address")
    private String address;

    @Schema(description = "Brand accent color (hex)", example = "#b8732b")
    private String brandColor;

    @Schema(description = "Whether the workspace is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Logo file id; null if unset")
    private UUID logoId;

    @Schema(description = "Owning organization id")
    private UUID organizationId;
}
