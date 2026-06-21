package com.gucardev.springreactboilerplate.features.news.model.request;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class NewsFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by title (contains, case-insensitive)", example = "menu")
    @Size(max = 300)
    private String title;

    @Schema(description = "Filter by featured flag", example = "true")
    private Boolean featured;

    @Schema(description = "Filter by a single tag (exact match)", example = "campaign")
    @Size(max = 50)
    private String tag;
}
