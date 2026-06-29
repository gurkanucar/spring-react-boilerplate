package com.gucardev.springreactboilerplate.features.core.user.domain.model;

/**
 * User-side selector for which backend a profile image should be stored in. Mirrors the file
 * feature's storage backends without coupling the user core to that feature's type — the file
 * adapter translates this to the file feature's {@code StorageType}.
 */
public enum ProfileImageStorageType {
    FILESYSTEM,
    S3,
    R2
}
