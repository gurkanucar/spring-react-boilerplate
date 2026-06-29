package com.gucardev.springreactboilerplate.features.core.file.application.service;

import com.gucardev.springreactboilerplate.features.core.file.application.port.in.GetFileUrlUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.LoadFilePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.FileUrl;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves a file's access URLs (main + thumbnail) via {@link FileUrlResolver}. The {@code getUrl}
 * variant is the cached, 404-on-miss read used by the URL endpoint; the {@code resolveUrl(s)}
 * variants are the null/absent-on-miss lookups used to enrich references to a file elsewhere.
 */
@Service
@RequiredArgsConstructor
public class GetFileUrlService implements GetFileUrlUseCase {

    private final FileFinder finder;
    private final LoadFilePort loadFilePort;
    private final FileUrlResolver fileUrlResolver;

    @Override
    @Transactional(readOnly = true)
    public FileUrl getUrl(UUID id) {
        return fileUrlResolver.resolve(finder.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public FileUrl resolveUrl(UUID id) {
        return loadFilePort.findById(id).map(fileUrlResolver::resolve).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, FileUrl> resolveUrls(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        return loadFilePort.findAllById(ids).stream()
                .collect(Collectors.toMap(StoredFile::getId, fileUrlResolver::resolve));
    }
}
