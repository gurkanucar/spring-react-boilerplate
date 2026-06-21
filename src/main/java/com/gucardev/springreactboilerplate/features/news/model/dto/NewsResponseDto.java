package com.gucardev.springreactboilerplate.features.news.model.dto;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
@Schema(description = "A news entry with its audit metadata.")
public class NewsResponseDto extends BaseDto {

    @Schema(description = "Identifier")
    private UUID id;

    @Schema(description = "Owning workspace id")
    private UUID workspaceId;

    @Schema(description = "Title", example = "New menu launched")
    private String title;

    @Schema(description = "URL-safe slug auto-generated from the title", example = "new-menu-launched")
    private String slug;

    @Schema(description = "Body content")
    private String content;

    @Schema(description = "Whether this entry is featured", example = "true")
    private Boolean featured;

    @Schema(description = "File id of the featured image; null if unset")
    private UUID featuredImageId;

    @Schema(description = "Ordered file ids of attached images")
    private List<UUID> imageIds;

    @Schema(description = "Ordered file ids of attached documents (any file type)")
    private List<UUID> attachmentIds;

    @Schema(description = "Tags", example = "[\"campaign\",\"menu\"]")
    private Set<String> tags;
}
