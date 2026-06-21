package com.gucardev.springreactboilerplate.features.core.notification.model.dto;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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
@Schema(description = "An in-app notification for the current user.")
public class NotificationDto extends BaseDto {

    @Schema(description = "Identifier")
    private UUID id;

    @Schema(description = "Machine-readable category", example = "NEWS_CREATED")
    private String type;

    @Schema(description = "Title", example = "News published")
    private String title;

    @Schema(description = "Body message")
    private String message;

    @Schema(description = "Whether the user has read it", example = "false")
    private Boolean read;

    @Schema(description = "When it was read; null if unread")
    private LocalDateTime readAt;
}
