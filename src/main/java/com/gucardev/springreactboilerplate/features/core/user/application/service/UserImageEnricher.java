package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.port.out.FileUrlLookupPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.ProfileImageUrls;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves and attaches profile-image URLs onto domain users (the derived presentation fields). A
 * single user is resolved lazily (one lookup); a collection is resolved as one batch to avoid N+1.
 */
@Service
@RequiredArgsConstructor
public class UserImageEnricher {

    private final FileUrlLookupPort fileUrlLookupPort;

    public void enrich(User user) {
        if (user == null || user.getProfileImageId() == null) {
            return;
        }
        applyUrls(user, fileUrlLookupPort.resolve(user.getProfileImageId()));
    }

    public void enrich(List<User> users) {
        Set<UUID> imageIds = users.stream()
                .map(User::getProfileImageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (imageIds.isEmpty()) {
            return;
        }
        Map<UUID, ProfileImageUrls> urlsByImageId = fileUrlLookupPort.resolveAll(imageIds);
        for (User user : users) {
            if (user.getProfileImageId() != null) {
                applyUrls(user, urlsByImageId.get(user.getProfileImageId()));
            }
        }
    }

    private void applyUrls(User user, ProfileImageUrls urls) {
        if (urls != null) {
            user.setProfileImageUrl(urls.url());
            user.setProfileImageThumbnailUrl(urls.thumbnailUrl());
        }
    }
}
