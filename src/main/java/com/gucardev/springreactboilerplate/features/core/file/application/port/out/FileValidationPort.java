package com.gucardev.springreactboilerplate.features.core.file.application.port.out;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.ValidatedUpload;

/**
 * Output port for upload validation: enforce the size cap and extension allow/deny policy (config),
 * and sniff the real content type from magic bytes, returning a {@link ValidatedUpload}. Implemented
 * by a driven adapter (Apache Tika + upload config) so the application core stays free of both the
 * mime library and the configuration binding.
 */
public interface FileValidationPort {

    ValidatedUpload validate(String originalFilename, byte[] content);
}
