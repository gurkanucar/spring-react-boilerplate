package com.gucardev.springreactboilerplate.features.core.user.adapter.out.file;

import com.gucardev.springreactboilerplate.features.core.file.application.port.in.DeleteFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadFileCommand;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadImageUseCase;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.ProfileImageStoragePort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.ProfileImageStorageType;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Driven adapter backing {@link ProfileImageStoragePort} by delegating to the file feature's image
 * pipeline ({@link UploadImageUseCase} / {@link DeleteFileUseCase} input ports). Reads the multipart
 * bytes into the file feature's {@link UploadFileCommand} and translates the user-side
 * {@link ProfileImageStorageType} to the file feature's {@link StorageType}. Confines the file-feature
 * dependency to this adapter.
 */
@Component
@RequiredArgsConstructor
public class ProfileImageStorageAdapter implements ProfileImageStoragePort {

    private final UploadImageUseCase uploadImageUseCase;
    private final DeleteFileUseCase deleteFileUseCase;

    @Override
    public UUID uploadImage(MultipartFile file, ProfileImageStorageType storageType) {
        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read profile image bytes", e);
        }
        StoredFile uploaded = uploadImageUseCase.uploadImage(
                new UploadFileCommand(file.getOriginalFilename(), content, toFileStorageType(storageType)));
        return uploaded.getId();
    }

    @Override
    public void deleteFile(UUID fileId) {
        deleteFileUseCase.delete(fileId);
    }

    private StorageType toFileStorageType(ProfileImageStorageType storageType) {
        return storageType == null ? null : StorageType.valueOf(storageType.name());
    }
}
