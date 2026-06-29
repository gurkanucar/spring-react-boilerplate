package com.gucardev.springreactboilerplate.features.core.file.application.port.in;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.FileUrl;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Input port: resolve a file's access URLs (public CDN URL, or the app download endpoint). Consumed
 * both by the file URL endpoint (404 on miss) and by other features enriching references to a file
 * (null/absent on miss, including a batch variant to avoid N+1).
 */
public interface GetFileUrlUseCase {

    /** Resolve the URLs for a file, or 404 if it does not exist. */
    FileUrl getUrl(UUID id);

    /** Resolve the URLs for a file, or {@code null} if it does not exist. */
    FileUrl resolveUrl(UUID id);

    /** Resolve URLs for many files in one query (ids with no file are simply absent from the map). */
    Map<UUID, FileUrl> resolveUrls(Set<UUID> ids);
}
