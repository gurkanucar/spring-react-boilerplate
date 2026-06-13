package com.gucardev.springreactboilerplate.domain.file.config;

import com.gucardev.springreactboilerplate.domain.file.enums.StorageType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds the {@code storage.*} block. Multiple backends can be active at once; {@code defaultType}
 * picks where new uploads go. Each backend has an {@code enabled} flag and is only registered when
 * its required settings are present (bucket + keys, plus endpoint for R2).
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    /** Backend new uploads are written to: FILESYSTEM | S3 | R2. */
    private StorageType defaultType = StorageType.FILESYSTEM;

    private Filesystem filesystem = new Filesystem();
    private S3 s3 = new S3();
    private R2 r2 = new R2();

    @Getter
    @Setter
    public static class Filesystem {
        private boolean enabled = true;
        /** Local directory uploads are written under. */
        private String basePath = "./uploads";
        /** Optional public base URL (e.g. an nginx/CDN in front of the dir); else the app serves them. */
        private String publicUrl;
    }

    @Getter
    @Setter
    public static class S3 {
        private boolean enabled = true;
        private String bucket;
        private String region = "us-east-1";
        private String endpoint;
        private String accessKey;
        private String secretKey;
        /** Public base = endpoint/bucket (path-style); {@code /files/{id}/url} returns base + key. */
        private String publicUrl;
    }

    @Getter
    @Setter
    public static class R2 {
        private boolean enabled = true;
        private String bucket;
        private String accessKey;
        private String secretKey;
        private String endpoint;
        private String publicUrl;
    }
}
