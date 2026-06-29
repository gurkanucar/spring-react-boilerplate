package com.gucardev.springreactboilerplate.features.core.file.domain.model;

/**
 * Which backend a file lives in. Stored on each file so it always resolves to the right provider,
 * independent of which backend new uploads currently target.
 */
public enum StorageType {
    FILESYSTEM,
    S3,
    R2
}
