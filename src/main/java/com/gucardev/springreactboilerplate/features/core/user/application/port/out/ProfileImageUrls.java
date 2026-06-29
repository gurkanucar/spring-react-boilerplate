package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

/**
 * User-side value type for a resolved profile image's access URLs, decoupling the user core from the
 * file feature's own URL DTO.
 */
public record ProfileImageUrls(
        String url,
        String thumbnailUrl
) {
}
