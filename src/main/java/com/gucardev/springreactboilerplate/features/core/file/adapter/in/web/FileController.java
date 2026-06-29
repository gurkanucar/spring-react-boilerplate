package com.gucardev.springreactboilerplate.features.core.file.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.file.adapter.in.web.dto.FileResponse;
import com.gucardev.springreactboilerplate.features.core.file.adapter.in.web.dto.FileUrlResponse;
import com.gucardev.springreactboilerplate.features.core.file.adapter.in.web.dto.StorageBackendsResponse;
import com.gucardev.springreactboilerplate.features.core.file.application.exception.FileExceptionType;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.DeleteFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.DownloadFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.GetFileUrlUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.GetFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.GetStorageBackendsUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadFileCommand;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadImageUseCase;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.FileContent;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
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
 *
 * <p>The controller only talks to input ports, extracts the multipart bytes into a transport-free
 * {@link UploadFileCommand}, and maps domain models to web DTOs.
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
    private final FileWebMapper fileWebMapper;

    @Operation(summary = "List the active storage backends (valid 'storageType' values) and the default")
    @GetMapping("/storage-backends")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<StorageBackendsResponse>> storageBackends() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                fileWebMapper.toResponse(getStorageBackendsUseCase.getBackends())));
    }

    @Operation(summary = "Upload a file (multipart 'file'). storageType selects the backend; omit for the default.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<FileResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "storageType", required = false) StorageType storageType) {
        FileResponse response = fileWebMapper.toResponse(
                uploadFileUseCase.upload(toCommand(file, storageType)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Upload an image (multipart 'file') — optimized to WebP, with a thumbnail")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<FileResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "storageType", required = false) StorageType storageType) {
        FileResponse response = fileWebMapper.toResponse(
                uploadImageUseCase.uploadImage(toCommand(file, storageType)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Get file metadata")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<FileResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(fileWebMapper.toResponse(getFileUseCase.getById(id))));
    }

    @Operation(summary = "Get the access URL (public CDN URL, or the app download endpoint)")
    @GetMapping("/{id}/url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<FileUrlResponse>> url(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(fileWebMapper.toResponse(getFileUrlUseCase.getUrl(id))));
    }

    @Operation(summary = "Download the file bytes through the app (public)")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        return toDownloadResponse(downloadFileUseCase.download(id));
    }

    @Operation(summary = "Download the thumbnail bytes (image uploads only, public)")
    @GetMapping("/{id}/thumbnail/download")
    public ResponseEntity<Resource> downloadThumbnail(@PathVariable UUID id) {
        return toDownloadResponse(downloadFileUseCase.downloadThumbnail(id));
    }

    @Operation(summary = "Delete a file (metadata + object + thumbnail)")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteFileUseCase.delete(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "File deleted"));
    }

    private UploadFileCommand toCommand(MultipartFile file, StorageType storageType) {
        return new UploadFileCommand(
                file == null ? null : file.getOriginalFilename(), readBytes(file), storageType);
    }

    private byte[] readBytes(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw FileExceptionType.EMPTY_FILE.toException();
        }
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw FileExceptionType.STORAGE_FAILURE.toException();
        }
    }

    private ResponseEntity<Resource> toDownloadResponse(FileContent file) {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(file.filename()).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.content().length)
                .body(new ByteArrayResource(file.content()));
    }
}
