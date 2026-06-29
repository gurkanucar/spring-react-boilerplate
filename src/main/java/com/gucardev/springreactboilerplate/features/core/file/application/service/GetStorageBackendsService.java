package com.gucardev.springreactboilerplate.features.core.file.application.service;

import com.gucardev.springreactboilerplate.features.core.file.application.port.in.GetStorageBackendsUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileStoragePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageBackends;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Reports which storage backends are active and which is the default, so a client can present the
 * valid options for the upload {@code storageType} selector.
 */
@Service
@RequiredArgsConstructor
public class GetStorageBackendsService implements GetStorageBackendsUseCase {

    private final FileStoragePort fileStoragePort;

    @Override
    public StorageBackends getBackends() {
        return new StorageBackends(fileStoragePort.activeTypes(), fileStoragePort.defaultType());
    }
}
