package com.gucardev.springreactboilerplate.features.core.file.service;

/**
 * The WebP-encoded outputs of optimizing an image upload: the (optionally downscaled) main image
 * and its thumbnail.
 */
public record OptimizedImage(byte[] main, byte[] thumbnail) {
}
