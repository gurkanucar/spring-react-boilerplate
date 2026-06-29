package com.gucardev.springreactboilerplate.features.core.file.application.port.out;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.OptimizedImage;

/**
 * Output port for image processing: re-encode an uploaded image to an optimized WebP and produce a
 * bounded thumbnail. Implemented by a driven adapter (Scrimage/native cwebp). The application core
 * depends only on this port.
 */
public interface ImageProcessingPort {

    String WEBP_CONTENT_TYPE = "image/webp";
    String WEBP_EXTENSION = ".webp";

    OptimizedImage optimize(byte[] source);
}
