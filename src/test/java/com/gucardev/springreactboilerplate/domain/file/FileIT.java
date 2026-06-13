package com.gucardev.springreactboilerplate.domain.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

/**
 * Coverage of the file upload/download flow against the filesystem backend (test profile). Verifies
 * metadata, url/download, delete, backend selection, and the upload guards.
 */
class FileIT extends BaseIntegrationTest {

    // A valid 1x1 PNG — Tika detects image/png from its signature.
    private static final byte[] PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+M8AAAMBAQDJ/pLvAAAAAElFTkSuQmCC");

    @Test
    @WithMockUser
    void upload_validPng_thenGet_url_download_delete() {
        JsonNode data = uploadMultipart("/files", PNG, "photo.png", MediaType.IMAGE_PNG, null, 201).path("data");
        String id = data.path("id").asText();
        assertThat(data.path("originalFilename").asText()).isEqualTo("photo.png");
        assertThat(data.path("contentType").asText()).isEqualTo("image/png");
        assertThat(data.path("extension").asText()).isEqualTo(".png");
        assertThat(data.path("size").asLong()).isEqualTo(PNG.length);
        assertThat(data.path("storageType").asText()).isEqualTo("FILESYSTEM");

        getJson("/files/" + id, 200);

        // url -> filesystem has no public URL, so the app download endpoint
        assertThat(getJson("/files/" + id + "/url", 200).path("data").path("url").asText())
                .isEqualTo("/files/" + id + "/download");

        // download returns the exact bytes
        EntityExchangeResult<byte[]> dl = client.get().uri("/files/" + id + "/download")
                .exchange().expectStatus().isOk()
                .expectBody(byte[].class).returnResult();
        assertThat(Objects.requireNonNull(dl.getResponseHeaders().getContentType())).hasToString("image/png");
        assertThat(dl.getResponseBody()).isEqualTo(PNG);

        deleteJson("/files/" + id, 200);
        getJson("/files/" + id, 404);
    }

    @Test
    @WithMockUser
    void upload_deniedExtension_isRejected() {
        JsonNode body = uploadMultipart("/files", new byte[]{'M', 'Z', 0, 1}, "malware.exe",
                MediaType.APPLICATION_OCTET_STREAM, null, 415);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_EXTENSION_NOT_ALLOWED");
    }

    @Test
    @WithMockUser
    void upload_contentDoesNotMatchExtension_isRejected() {
        // PNG bytes disguised as a .pdf -> magic-byte check catches the mismatch.
        JsonNode body = uploadMultipart("/files", PNG, "document.pdf", MediaType.APPLICATION_PDF, null, 415);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_CONTENT_MISMATCH");
    }

    @Test
    @WithMockUser
    void upload_emptyFile_isRejected() {
        JsonNode body = uploadMultipart("/files", new byte[0], "empty.png", MediaType.IMAGE_PNG, null, 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_EMPTY");
    }

    @Test
    @WithMockUser
    void upload_toExplicitActiveBackend_succeeds() {
        JsonNode data = uploadMultipart("/files", PNG, "pic.png", MediaType.IMAGE_PNG,
                Map.of("storageType", "FILESYSTEM"), 201).path("data");
        assertThat(data.path("storageType").asText()).isEqualTo("FILESYSTEM");
    }

    @Test
    @WithMockUser
    void upload_toInactiveBackend_isRejected() {
        // S3 isn't configured in the test profile -> selecting it is a 400.
        JsonNode body = uploadMultipart("/files", PNG, "pic.png", MediaType.IMAGE_PNG,
                Map.of("storageType", "S3"), 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("STORAGE_BACKEND_NOT_ACTIVE");
    }

    @Test
    @WithMockUser
    void storageBackends_listsActiveAndDefault() {
        JsonNode data = getJson("/files/storage-backends", 200).path("data");
        assertThat(data.path("active").toString()).contains("FILESYSTEM");
        assertThat(data.path("defaultType").asText()).isEqualTo("FILESYSTEM");
    }

    @Test
    void download_isPublic_reachesControllerWithoutAuth() {
        // No @WithMockUser: a public endpoint reaches the controller (404 for a missing id), not 401.
        client.get().uri("/files/" + UUID.randomUUID() + "/download").exchange().expectStatus().isNotFound();
        client.get().uri("/files/" + UUID.randomUUID() + "/thumbnail/download").exchange().expectStatus().isNotFound();
    }

    @Test
    void upload_withoutAuth_isUnauthorized() {
        multipart("/files", PNG, "x.png", MediaType.IMAGE_PNG, null).expectStatus().isUnauthorized();
    }
}
