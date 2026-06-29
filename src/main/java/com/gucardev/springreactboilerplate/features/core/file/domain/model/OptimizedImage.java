package com.gucardev.springreactboilerplate.features.core.file.domain.model;

/**
 * The WebP-encoded outputs of optimizing an image upload: the (optionally downscaled) main image
 * and its thumbnail.
 */
public record OptimizedImage(byte[] main, byte[] thumbnail) {
}
