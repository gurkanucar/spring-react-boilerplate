package com.gucardev.springreactboilerplate.features.core.file.domain.model;

/**
 * Resolved access URLs for a file: the public CDN URL (or the app download endpoint) for the main
 * object and, for image uploads, its thumbnail. {@code thumbnailUrl} is null for non-image files.
 */
public record FileUrl(String url, String thumbnailUrl) {
}
