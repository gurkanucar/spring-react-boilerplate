package com.gucardev.springreactboilerplate.domain.file.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resolved access URLs for a file (public CDN URL, or the app download endpoint).")
public record FileUrlDto(

        @Schema(description = "Where to fetch the file", example = "https://cdn.example.org/3f1e7c9a-....webp")
        String url,

        @Schema(description = "Where to fetch the thumbnail (image uploads only); null otherwise",
                example = "https://cdn.example.org/3f1e7c9a-..._tb.webp")
        String thumbnailUrl
) {
}
