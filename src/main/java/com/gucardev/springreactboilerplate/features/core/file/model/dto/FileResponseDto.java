package com.gucardev.springreactboilerplate.features.core.file.model.dto;

import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;
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
@Schema(description = "Stored file metadata. Fetch the access URL via GET /files/{id}/url.")
public class FileResponseDto extends BaseDto {

    @Schema(description = "Identifier (also the basis of the storage key)", example = "3f1e7c9a-2b6d-4c8e-9f0a-1d2e3f4a5b6c")
    private UUID id;

    @Schema(description = "Sanitized original filename", example = "invoice_2026.pdf")
    private String originalFilename;

    @Schema(description = "Magic-byte-detected content type", example = "application/pdf")
    private String contentType;

    @Schema(description = "Size in bytes", example = "20480")
    private Long size;

    @Schema(description = "File extension", example = ".pdf")
    private String extension;

    @Schema(description = "Backend the file is stored in", example = "S3")
    private StorageType storageType;
}
