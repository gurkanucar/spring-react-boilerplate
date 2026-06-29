package com.gucardev.springreactboilerplate.features.core.file.domain.model;

import java.util.Set;

/**
 * The storage backends available for uploads: those currently active and the one used when no
 * {@code storageType} is specified.
 */
public record StorageBackends(Set<StorageType> active, StorageType defaultType) {
}
