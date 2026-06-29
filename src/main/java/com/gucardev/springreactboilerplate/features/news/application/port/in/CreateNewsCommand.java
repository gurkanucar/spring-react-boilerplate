package com.gucardev.springreactboilerplate.features.news.application.port.in;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Driving-side command for creating a news entry. Carries already-validated input from a driving
 * adapter into the application core, decoupling the core from any particular transport.
 */
public record CreateNewsCommand(
        String title,
        String content,
        Boolean featured,
        List<UUID> imageIds,
        UUID featuredImageId,
        List<UUID> attachmentIds,
        Set<String> tags
) {
}
