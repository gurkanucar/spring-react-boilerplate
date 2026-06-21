package com.gucardev.springreactboilerplate.features.core.file.service.usecase;

import com.gucardev.springreactboilerplate.features.core.file.model.dto.StorageBackendsDto;
import com.gucardev.springreactboilerplate.features.core.file.storage.StorageProviderRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Reports which storage backends are active and which is the default, so a client can present the
 * valid options for the upload {@code storageType} selector.
 */
@Service
@RequiredArgsConstructor
public class GetStorageBackendsUseCase {

    private final StorageProviderRegistry storageRegistry;

    public StorageBackendsDto execute() {
        return new StorageBackendsDto(storageRegistry.activeTypes(), storageRegistry.defaultType());
    }
}
