package com.gucardev.springreactboilerplate.domain.file.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds the {@code upload.*} block: the size cap and the extension allow/deny lists used by the
 * upload validator. Extensions are lower-case and include the leading dot (e.g. {@code .exe}).
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    /** Hard size limit per file, in bytes. */
    private long maxBytes = 52_428_800L; // 50 MB

    /** Extensions that are always rejected. */
    private List<String> denyExt = List.of();

    /** If non-empty, ONLY these extensions are accepted (whitelist). Empty = allow anything not denied. */
    private List<String> allowExt = List.of();
}
