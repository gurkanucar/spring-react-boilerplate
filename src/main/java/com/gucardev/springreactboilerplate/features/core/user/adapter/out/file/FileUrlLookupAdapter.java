package com.gucardev.springreactboilerplate.features.core.user.adapter.out.file;

import com.gucardev.springreactboilerplate.features.core.file.application.port.in.GetFileUrlUseCase;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.FileUrl;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.FileUrlLookupPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.ProfileImageUrls;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link FileUrlLookupPort} by delegating to the file feature's
 * {@link GetFileUrlUseCase} input port, mapping its {@link FileUrl} to the user-side
 * {@link ProfileImageUrls}. Confines the file-feature dependency to this adapter.
 */
@Component
@RequiredArgsConstructor
public class FileUrlLookupAdapter implements FileUrlLookupPort {

    private final GetFileUrlUseCase getFileUrlUseCase;

    @Override
    public ProfileImageUrls resolve(UUID fileId) {
        FileUrl url = getFileUrlUseCase.resolveUrl(fileId);
        return url == null ? null : toUrls(url);
    }

    @Override
    public Map<UUID, ProfileImageUrls> resolveAll(Set<UUID> fileIds) {
        if (fileIds.isEmpty()) {
            return Map.of();
        }
        return getFileUrlUseCase.resolveUrls(fileIds).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> toUrls(entry.getValue())));
    }

    private ProfileImageUrls toUrls(FileUrl url) {
        return new ProfileImageUrls(url.url(), url.thumbnailUrl());
    }
}
