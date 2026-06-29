package com.gucardev.springreactboilerplate.features.core.file.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "Storage backends available for uploads.")
public record StorageBackendsResponse(

        @Schema(description = "Backends currently active (valid values for the upload storageType param)",
                example = "[\"FILESYSTEM\",\"S3\",\"R2\"]")
        Set<StorageType> active,

        @Schema(description = "Backend used when storageType is omitted", example = "FILESYSTEM")
        StorageType defaultType
) {
}
