package com.gucardev.springreactboilerplate.features.core.file.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds {@code image.*}: how image uploads are optimized. Images are re-encoded to WebP at
 * {@code quality}, optionally bounded by {@code maxDimension}, and a {@code thumbnailSize} thumbnail
 * is produced alongside.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "image")
public class ImageProperties {

    /** WebP quality 0..100 (lower = smaller/lossier). */
    private int quality = 80;

    /** Longest side of the optimized main image; 0 = don't downscale. */
    private int maxDimension = 2000;

    /** Longest side of the generated thumbnail. */
    private int thumbnailSize = 256;
}
