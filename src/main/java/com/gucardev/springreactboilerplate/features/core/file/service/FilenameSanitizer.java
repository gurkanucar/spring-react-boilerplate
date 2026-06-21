package com.gucardev.springreactboilerplate.features.core.file.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Sanitizes a client-supplied filename into something safe to keep and echo back: strips any path
 * components (defeats traversal like {@code ../../etc/passwd} and Windows paths), removes control
 * characters, replaces anything outside {@code [A-Za-z0-9._-]} with {@code _}, and caps the length.
 * The stored object key is the file UUID, not this name — so this is purely for the display name.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilenameSanitizer {

    private static final int MAX_LENGTH = 200;
    private static final String FALLBACK = "file";

    public static String sanitize(String original) {
        if (original == null || original.isBlank()) {
            return FALLBACK;
        }
        // Strip any directory components (both / and \).
        String name = original.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1);
        // Drop control chars, then replace unsafe chars.
        name = name.replaceAll("\\p{Cntrl}", "");
        name = name.replaceAll("[^A-Za-z0-9._-]", "_");
        // Collapse runs of underscores and strip leading dots (no hidden/relative names).
        name = name.replaceAll("_+", "_");
        name = name.replaceAll("^\\.+", "");
        if (name.isBlank()) {
            return FALLBACK;
        }
        return name.length() > MAX_LENGTH ? name.substring(0, MAX_LENGTH) : name;
    }

    /** The filename without its extension (e.g. {@code photo.png} -> {@code photo}). */
    public static String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }

    /** Lower-cased extension including the leading dot (e.g. {@code .png}), or empty if none. */
    public static String extension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot).toLowerCase();
    }
}
