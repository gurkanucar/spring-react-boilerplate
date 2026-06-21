package com.gucardev.springreactboilerplate.features.tenancy.organization.model.dto;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
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
@Schema(description = "Organization (tenant) with its audit metadata.")
public class OrganizationResponseDto extends BaseDto {

    @Schema(description = "Identifier", example = "7a2b1c9d-3e4f-5a6b-7c8d-9e0f1a2b3c4d")
    private UUID id;

    @Schema(description = "Display name", example = "Acme Inc.")
    private String name;

    @Schema(description = "URL-safe unique slug", example = "acme")
    private String slug;

    @Schema(description = "Free-text description")
    private String description;

    @Schema(description = "Phone number", example = "+1-555-0100")
    private String phoneNumber;

    @Schema(description = "Address")
    private String address;

    @Schema(description = "Whether the organization is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Logo file id (resolve URL via GET /files/{id}/url); null if unset")
    private UUID logoId;
}
