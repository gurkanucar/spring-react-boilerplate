package com.gucardev.springreactboilerplate.features.core.file.application.port.in;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageBackends;

/**
 * Input port: report which storage backends are active and which is the default, so a client can
 * present the valid options for the upload {@code storageType} selector.
 */
public interface GetStorageBackendsUseCase {

    StorageBackends getBackends();
}
