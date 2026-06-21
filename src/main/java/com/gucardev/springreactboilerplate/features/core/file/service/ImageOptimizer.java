package com.gucardev.springreactboilerplate.features.core.file.service;

import com.gucardev.springreactboilerplate.features.core.file.config.ImageProperties;
import com.gucardev.springreactboilerplate.features.core.file.exception.FileExceptionType;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Re-encodes an uploaded image to WebP (quality from config) and produces a bounded thumbnail, using
 * Scrimage (which bundles the native {@code cwebp} encoder). The output is the optimized main image
 * plus its thumbnail; the original bytes are discarded.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageOptimizer {

    public static final String WEBP_CONTENT_TYPE = "image/webp";
    public static final String WEBP_EXTENSION = ".webp";

    private final ImageProperties imageProperties;

    public OptimizedImage optimize(byte[] source) {
        try {
            ImmutableImage image = ImmutableImage.loader().fromBytes(source);
            WebpWriter writer = WebpWriter.DEFAULT.withQ(imageProperties.getQuality());

            int maxDim = imageProperties.getMaxDimension();
            ImmutableImage main = maxDim > 0 ? image.bound(maxDim, maxDim) : image;
            byte[] mainBytes = main.bytes(writer);

            int thumbSize = imageProperties.getThumbnailSize();
            byte[] thumbBytes = image.bound(thumbSize, thumbSize).bytes(writer);

            return new OptimizedImage(mainBytes, thumbBytes);
        } catch (Exception e) {
            log.warn("Image optimization failed: {}", e.getMessage());
            throw FileExceptionType.IMAGE_PROCESSING_FAILED.toException();
        }
    }
}
