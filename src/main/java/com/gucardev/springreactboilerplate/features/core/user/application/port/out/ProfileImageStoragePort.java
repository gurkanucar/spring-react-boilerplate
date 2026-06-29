package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.ProfileImageStorageType;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

/**
 * Output port: store and delete profile-image files. Backed by a driven adapter that delegates to
 * the file feature's image pipeline (optimized WebP + thumbnail), keeping the user core off the file
 * internals.
 */
public interface ProfileImageStoragePort {

    /**
     * Uploads and optimizes an image, returning the id of the stored file.
     *
     * @param storageType backend to store the image in; {@code null} uses the configured default.
     */
    UUID uploadImage(MultipartFile file, ProfileImageStorageType storageType);

    /** Deletes a stored file by id. May throw if the file is already gone. */
    void deleteFile(UUID fileId);
}
