package com.gucardev.springreactboilerplate.features.news.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Payload for updating a news entry. Null fields are left unchanged. The slug is "
        + "not regenerated on update so existing links stay valid.")
public record UpdateNewsRequest(

        @Schema(description = "Title")
        @Size(max = 300)
        String title,

        @Schema(description = "Body content")
        @Size(max = 50000)
        String content,

        @Schema(description = "Whether this entry is featured")
        Boolean featured,

        @Schema(description = "Ordered file ids of attached images (replaces the existing list when provided)")
        List<UUID> imageIds,

        @Schema(description = "File id of the featured image; must be one of imageIds")
        UUID featuredImageId,

        @Schema(description = "Ordered file ids of attached documents (replaces the existing list when provided)")
        List<UUID> attachmentIds,

        @Schema(description = "Tags (replaces the existing set when provided)", example = "[\"campaign\"]")
        Set<@Size(max = 50) String> tags
) {
}
