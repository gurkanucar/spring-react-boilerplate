package com.gucardev.springreactboilerplate.features.news.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Payload for creating a news entry in the active workspace. The slug is generated "
        + "from the title automatically. Upload images via the file API first, then pass their ids here.")
public record CreateNewsRequest(

        @Schema(description = "Title", example = "New menu launched")
        @NotBlank
        @Size(max = 300)
        String title,

        @Schema(description = "Body content")
        @Size(max = 50000)
        String content,

        @Schema(description = "Whether this entry is featured (defaults to false)", example = "false")
        Boolean featured,

        @Schema(description = "Ordered file ids of attached images")
        @Size(max = 100)
        List<UUID> imageIds,

        @Schema(description = "File id of the featured image; must be one of imageIds")
        UUID featuredImageId,

        @Schema(description = "Ordered file ids of attached documents (any file type)")
        @Size(max = 100)
        List<UUID> attachmentIds,

        @Schema(description = "Tags", example = "[\"campaign\",\"menu\"]")
        @Size(max = 50)
        Set<@Size(max = 50) String> tags
) {
}
