package com.gucardev.springreactboilerplate.features.core.file.config;

import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;
import com.gucardev.springreactboilerplate.features.core.file.storage.FilesystemStorageProvider;
import com.gucardev.springreactboilerplate.features.core.file.storage.R2StorageProvider;
import com.gucardev.springreactboilerplate.features.core.file.storage.S3StorageProvider;
import com.gucardev.springreactboilerplate.features.core.file.storage.StorageProvider;
import com.gucardev.springreactboilerplate.features.core.file.storage.StorageProviderRegistry;
import java.net.URI;
import java.util.EnumMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Builds the active storage providers from configuration. Each backend is registered only when its
 * enable flag is on and the required settings are present (bucket + keys, plus endpoint for R2), so
 * the app runs with whichever subset is configured — filesystem alone needs no setup.
 */
@Slf4j
@Configuration
public class StorageConfig {

    @Bean
    public StorageProviderRegistry storageProviderRegistry(StorageProperties props) {
        Map<StorageType, StorageProvider> providers = new EnumMap<>(StorageType.class);

        if (props.getFilesystem().isEnabled()) {
            providers.put(StorageType.FILESYSTEM, new FilesystemStorageProvider(props.getFilesystem()));
        }

        StorageProperties.S3 s3 = props.getS3();
        if (s3.isEnabled() && configured(s3.getBucket(), s3.getAccessKey(), s3.getSecretKey())) {
            S3Client client = s3Client(s3.getEndpoint(), s3.getRegion(), s3.getAccessKey(), s3.getSecretKey());
            providers.put(StorageType.S3, new S3StorageProvider(client, s3.getBucket(), s3.getPublicUrl()));
        }

        StorageProperties.R2 r2 = props.getR2();
        if (r2.isEnabled() && configured(r2.getBucket(), r2.getAccessKey(), r2.getSecretKey())
                && StringUtils.hasText(r2.getEndpoint())) {
            S3Client client = s3Client(r2.getEndpoint(), "auto", r2.getAccessKey(), r2.getSecretKey());
            providers.put(StorageType.R2, new R2StorageProvider(client, r2.getBucket(), r2.getPublicUrl()));
        }

        return new StorageProviderRegistry(providers, props.getDefaultType());
    }

    private boolean configured(String bucket, String accessKey, String secretKey) {
        return StringUtils.hasText(bucket) && StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey);
    }

    /** S3 client for an S3-compatible endpoint (path-style so RustFS/MinIO/R2 all work). */
    private S3Client s3Client(String endpoint, String region, String accessKey, String secretKey) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();
    }
}
