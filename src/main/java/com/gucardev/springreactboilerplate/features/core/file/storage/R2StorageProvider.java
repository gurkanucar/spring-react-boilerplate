package com.gucardev.springreactboilerplate.features.core.file.storage;

import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Cloudflare R2 backend. Same S3 API as {@link S3StorageProvider}; public access is via the
 * configured R2 public URL (custom domain), so there is no bucket-policy step.
 */
public class R2StorageProvider extends AbstractS3StorageProvider {

    public R2StorageProvider(S3Client client, String bucket, String publicUrl) {
        super(client, bucket, publicUrl);
    }

    @Override
    public StorageType type() {
        return StorageType.R2;
    }
}
