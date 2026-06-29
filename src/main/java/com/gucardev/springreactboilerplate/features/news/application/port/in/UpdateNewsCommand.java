package com.gucardev.springreactboilerplate.features.news.application.port.in;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Driving-side command for updating a news entry. Null fields are left unchanged; the slug is not
 * regenerated on update so existing links stay valid.
 */
public record UpdateNewsCommand(
        UUID id,
        String title,
        String content,
        Boolean featured,
        List<UUID> imageIds,
        UUID featuredImageId,
        List<UUID> attachmentIds,
        Set<String> tags
) {
}
