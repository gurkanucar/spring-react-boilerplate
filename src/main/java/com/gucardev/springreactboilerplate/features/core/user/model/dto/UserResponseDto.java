package com.gucardev.springreactboilerplate.features.core.user.model.dto;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
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
@Schema(description = "Authenticated user profile with its audit metadata.")
public class UserResponseDto extends BaseDto {

    @Schema(description = "Identifier", example = "3f1e7c9a-2b6d-4c8e-9f0a-1d2e3f4a5b6c")
    private UUID id;

    @Schema(description = "Email address (login)", example = "user@mail.com")
    private String email;

    @Schema(description = "First name", example = "Jane")
    private String name;

    @Schema(description = "Last name", example = "Doe")
    private String surname;

    @Schema(description = "Phone number", example = "+1-555-0100")
    private String phoneNumber;

    @Schema(description = "Profile image file id; null if unset",
            example = "3f1e7c9a-2b6d-4c8e-9f0a-1d2e3f4a5b6c")
    private UUID profileImageId;

    @Schema(description = "Organization the user belongs to; null for a global super-admin",
            example = "7a2b1c9d-3e4f-5a6b-7c8d-9e0f1a2b3c4d")
    private UUID organizationId;

    @Schema(description = "Workspace the user is pinned to (an employee); null for org-level users")
    private UUID workspaceId;

    @Schema(description = "Resolved profile image URL (CDN or app endpoint); null if unset")
    private String profileImageUrl;

    @Schema(description = "Resolved profile image thumbnail URL; null if unset")
    private String profileImageThumbnailUrl;

    @Schema(description = "Whether the email has been verified", example = "true")
    private Boolean activated;

    @Schema(description = "Whether the account is enabled", example = "true")
    private Boolean isActive;

    @Schema(description = "Role names granted to the user", example = "[\"ADMIN\",\"USER\"]")
    private Set<String> roles;
}
