package com.gucardev.springreactboilerplate.features.core.file.controller;

import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;
import com.gucardev.springreactboilerplate.features.core.file.model.dto.FileDownload;
import com.gucardev.springreactboilerplate.features.core.file.model.dto.FileResponseDto;
import com.gucardev.springreactboilerplate.features.core.file.model.dto.FileUrlDto;
import com.gucardev.springreactboilerplate.features.core.file.model.dto.StorageBackendsDto;
import com.gucardev.springreactboilerplate.features.core.file.service.usecase.DeleteFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.service.usecase.DownloadFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.service.usecase.GetFileUrlUseCase;
import com.gucardev.springreactboilerplate.features.core.file.service.usecase.GetFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.service.usecase.GetStorageBackendsUseCase;
import com.gucardev.springreactboilerplate.features.core.file.service.usecase.UploadFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.service.usecase.UploadImageUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * File upload/download API. All endpoints require authentication; public access to a file happens
 * via its CDN URL (see {@code GET /files/{id}/url}), not through these endpoints.
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "Upload, fetch and delete stored files (S3 / R2 / filesystem).")
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;
    private final UploadImageUseCase uploadImageUseCase;
    private final GetStorageBackendsUseCase getStorageBackendsUseCase;
    private final GetFileUseCase getFileUseCase;
    private final GetFileUrlUseCase getFileUrlUseCase;
    private final DownloadFileUseCase downloadFileUseCase;
    private final DeleteFileUseCase deleteFileUseCase;

    @Operation(summary = "List the active storage backends (valid 'storageType' values) and the default")
    @GetMapping("/storage-backends")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<StorageBackendsDto>> storageBackends() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getStorageBackendsUseCase.execute()));
    }

    @Operation(summary = "Upload a file (multipart 'file'). storageType selects the backend; omit for the default.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<FileResponseDto>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "storageType", required = false) StorageType storageType) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(uploadFileUseCase.execute(file, storageType)));
    }

    @Operation(summary = "Upload an image (multipart 'file') — optimized to WebP, with a thumbnail")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<FileResponseDto>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "storageType", required = false) StorageType storageType) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(uploadImageUseCase.execute(file, storageType)));
    }

    @Operation(summary = "Get file metadata")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<FileResponseDto>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getFileUseCase.execute(id)));
    }

    @Operation(summary = "Get the access URL (public CDN URL, or the app download endpoint)")
    @GetMapping("/{id}/url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<FileUrlDto>> url(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getFileUrlUseCase.execute(id)));
    }

    @Operation(summary = "Download the file bytes through the app (public)")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        FileDownload file = downloadFileUseCase.execute(id);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(file.filename()).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.content().length)
                .body(new ByteArrayResource(file.content()));
    }

    @Operation(summary = "Download the thumbnail bytes (image uploads only, public)")
    @GetMapping("/{id}/thumbnail/download")
    public ResponseEntity<Resource> downloadThumbnail(@PathVariable UUID id) {
        FileDownload file = downloadFileUseCase.executeThumbnail(id);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(file.filename()).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.content().length)
                .body(new ByteArrayResource(file.content()));
    }

    @Operation(summary = "Delete a file (metadata + object + thumbnail)")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteFileUseCase.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "File deleted"));
    }
}
