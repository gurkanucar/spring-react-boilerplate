package com.gucardev.springreactboilerplate.features.core.file.application.port.in;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.FileContent;
import java.util.UUID;

/**
 * Input port: stream a file's bytes (and its thumbnail) back through the app, for any backend.
 */
public interface DownloadFileUseCase {

    FileContent download(UUID id);

    FileContent downloadThumbnail(UUID id);
}
