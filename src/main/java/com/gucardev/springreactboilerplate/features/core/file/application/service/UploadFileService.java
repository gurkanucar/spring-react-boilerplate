package com.gucardev.springreactboilerplate.features.core.file.application.service;

import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadFileCommand;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileStoragePort;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileValidationPort;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.SaveFilePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.ValidatedUpload;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Validates an upload (size, extension allow/deny, magic-byte content check), assigns a UUID, writes
 * the bytes to the chosen backend keyed by {@code uuid + extension}, and persists the metadata.
 *
 * <p>Metadata is saved before the bytes are written, inside the transaction: if the backend write
 * fails the row is rolled back, so there is never a metadata row pointing at a missing object.
 */
@Service
@RequiredArgsConstructor
public class UploadFileService implements UploadFileUseCase {

    private final FileValidationPort validationPort;
    private final FileStoragePort fileStoragePort;
    private final SaveFilePort saveFilePort;

    @Override
    @Transactional
    public StoredFile upload(UploadFileCommand command) {
        ValidatedUpload upload = validationPort.validate(command.originalFilename(), command.content());

        UUID id = UUID.randomUUID();
        String key = id + upload.extension();
        StorageType target = fileStoragePort.resolveUploadTarget(command.storageType());

        StoredFile stored = saveFilePort.save(StoredFile.builder()
                .id(id)
                .originalFilename(upload.sanitizedFilename())
                .storageKey(key)
                .storageType(target)
                .contentType(upload.contentType())
                .size((long) upload.content().length)
                .extension(upload.extension())
                .build());

        fileStoragePort.store(target, key, upload.content(), upload.contentType());

        return stored;
    }
}
