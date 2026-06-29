package com.gucardev.springreactboilerplate.features.core.file.adapter.out.storage;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3-compatible backend (RustFS/MinIO/AWS). Objects are reachable at the configured public URL; the
 * bucket is expected to already be configured for public read.
 */
public class S3StorageProvider extends AbstractS3StorageProvider {

    public S3StorageProvider(S3Client client, String bucket, String publicUrl) {
        super(client, bucket, publicUrl);
    }

    @Override
    public StorageType type() {
        return StorageType.S3;
    }
}
