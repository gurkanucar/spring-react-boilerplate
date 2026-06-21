package com.gucardev.springreactboilerplate.features.core.file.service;

import com.gucardev.springreactboilerplate.features.core.file.config.UploadProperties;
import com.gucardev.springreactboilerplate.features.core.file.exception.FileExceptionType;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates an upload before it is stored:
 * <ol>
 *   <li>not empty, within {@code upload.max-bytes};</li>
 *   <li>claimed extension passes the deny-list and (if set) the allow-list;</li>
 *   <li>the <em>real</em> content type is sniffed from magic bytes (Apache Tika), not the client
 *       header — and the claimed extension must be consistent with it, so a renamed {@code .exe}
 *       posing as {@code .jpg} is rejected and the real type's extension can't be a denied one.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class FileValidator {

    private static final String OCTET_STREAM = "application/octet-stream";

    private final UploadProperties uploadProperties;
    private final Tika tika = new Tika();
    private final MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();

    public ValidatedUpload validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw FileExceptionType.EMPTY_FILE.toException();
        }
        byte[] content = readBytes(file);
        if (content.length == 0) {
            throw FileExceptionType.EMPTY_FILE.toException();
        }
        if (content.length > uploadProperties.getMaxBytes()) {
            throw FileExceptionType.FILE_TOO_LARGE.toException(uploadProperties.getMaxBytes());
        }

        String filename = FilenameSanitizer.sanitize(file.getOriginalFilename());
        String ext = FilenameSanitizer.extension(filename);

        Set<String> deny = normalize(uploadProperties.getDenyExt());
        Set<String> allow = normalize(uploadProperties.getAllowExt());

        if (deny.contains(ext)) {
            throw FileExceptionType.EXTENSION_NOT_ALLOWED.toException(ext);
        }
        if (!allow.isEmpty() && (ext.isEmpty() || !allow.contains(ext))) {
            throw FileExceptionType.EXTENSION_NOT_ALLOWED.toException(ext.isEmpty() ? "(none)" : ext);
        }

        String detected = tika.detect(content);
        List<String> detectedExts = extensionsFor(detected);

        if (!OCTET_STREAM.equals(detected) && !detectedExts.isEmpty()) {
            // The real content's type must not itself be a denied extension (renamed malware).
            for (String realExt : detectedExts) {
                if (deny.contains(realExt)) {
                    throw FileExceptionType.EXTENSION_NOT_ALLOWED.toException(realExt);
                }
            }
            // The declared extension must match the real content.
            if (!ext.isEmpty() && !detectedExts.contains(ext)) {
                throw FileExceptionType.CONTENT_TYPE_MISMATCH.toException(ext, detected);
            }
            // No declared extension -> adopt the real one.
            if (ext.isEmpty()) {
                ext = detectedExts.get(0);
            }
        }

        return new ValidatedUpload(filename, ext, detected, content);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw FileExceptionType.STORAGE_FAILURE.toException();
        }
    }

    private List<String> extensionsFor(String mime) {
        try {
            return mimeTypes.forName(mime).getExtensions();
        } catch (MimeTypeException e) {
            return List.of();
        }
    }

    private Set<String> normalize(List<String> extensions) {
        return extensions.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.startsWith(".") ? s : "." + s)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
