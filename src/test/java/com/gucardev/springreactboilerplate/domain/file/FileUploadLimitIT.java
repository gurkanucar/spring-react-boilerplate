package com.gucardev.springreactboilerplate.domain.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

/**
 * Verifies the {@code upload.max-bytes} cap. Sets a tiny limit so a normal small file trips it with
 * the domain FILE_TOO_LARGE error (not the raw container limit).
 */
@TestPropertySource(properties = "upload.max-bytes=8")
class FileUploadLimitIT extends BaseIntegrationTest {

    private static final byte[] PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+M8AAAMBAQDJ/pLvAAAAAElFTkSuQmCC");

    @Test
    @WithMockUser
    void upload_overMaxBytes_isRejected() {
        JsonNode body = uploadMultipart("/files", PNG, "photo.png", MediaType.IMAGE_PNG, null, 413);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_TOO_LARGE");
    }
}
