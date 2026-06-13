package com.gucardev.springreactboilerplate.domain.file.storage;

import com.gucardev.springreactboilerplate.domain.file.exception.FileExceptionType;
import java.io.Closeable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Shared S3-compatible behaviour (RustFS/MinIO/AWS and Cloudflare R2 all speak the S3 API). Holds
 * the {@link S3Client}, bucket and public-URL base; subclasses just declare their {@code type()}.
 */
@Slf4j
public abstract class AbstractS3StorageProvider implements StorageProvider, Closeable {

    protected final S3Client client;
    protected final String bucket;
    protected final String publicUrl;

    protected AbstractS3StorageProvider(S3Client client, String bucket, String publicUrl) {
        this.client = client;
        this.bucket = bucket;
        this.publicUrl = publicUrl;
    }

    @Override
    public void store(String key, byte[] content, String contentType) {
        try {
            client.putObject(b -> b.bucket(bucket).key(key).contentType(contentType),
                    RequestBody.fromBytes(content));
        } catch (S3Exception e) {
            log.error("S3 putObject failed for {}/{}: {}", bucket, key, e.getMessage());
            throw FileExceptionType.STORAGE_FAILURE.toException();
        }
    }

    @Override
    public byte[] retrieve(String key) {
        try {
            return client.getObjectAsBytes(b -> b.bucket(bucket).key(key)).asByteArray();
        } catch (NoSuchKeyException e) {
            throw FileExceptionType.NOT_FOUND.toException();
        } catch (S3Exception e) {
            log.error("S3 getObject failed for {}/{}: {}", bucket, key, e.getMessage());
            throw FileExceptionType.STORAGE_FAILURE.toException();
        }
    }

    @Override
    public void delete(String key) {
        try {
            client.deleteObject(b -> b.bucket(bucket).key(key));
        } catch (S3Exception e) {
            log.warn("S3 deleteObject failed for {}/{}: {}", bucket, key, e.getMessage());
        }
    }

    @Override
    public String publicUrl(String key) {
        return StringUtils.hasText(publicUrl) ? trimSlash(publicUrl) + "/" + key : null;
    }

    @Override
    public void close() {
        client.close();
    }

    protected String trimSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}
