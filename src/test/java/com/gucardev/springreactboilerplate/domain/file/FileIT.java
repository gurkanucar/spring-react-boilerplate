package com.gucardev.springreactboilerplate.domain.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Coverage of the file upload/download flow against the filesystem backend (test profile). Verifies
 * metadata, url/download, delete, and the upload guards: extension deny-list, magic-byte content vs
 * declared-extension mismatch, empty files, and that the endpoints require authentication.
 */
class FileIT extends BaseIntegrationTest {

    // A valid 1x1 PNG — Tika detects image/png from its signature.
    private static final byte[] PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+M8AAAMBAQDJ/pLvAAAAAElFTkSuQmCC");

    @Test
    @WithMockUser
    void upload_validPng_thenGet_url_download_delete() throws Exception {
        // upload
        JsonNode data = upload("photo.png", "image/png", PNG, 201).path("data");
        String id = data.path("id").asText();
        assertThat(data.path("originalFilename").asText()).isEqualTo("photo.png");
        assertThat(data.path("contentType").asText()).isEqualTo("image/png");
        assertThat(data.path("extension").asText()).isEqualTo(".png");
        assertThat(data.path("size").asLong()).isEqualTo(PNG.length);
        assertThat(data.path("storageType").asText()).isEqualTo("FILESYSTEM");

        // get metadata
        getJson("/files/" + id, 200);

        // url -> filesystem has no public URL configured, so the app download endpoint
        JsonNode url = getJson("/files/" + id + "/url", 200).path("data");
        assertThat(url.path("url").asText()).isEqualTo("/files/" + id + "/download");

        // download returns the exact bytes
        MvcResult dl = mockMvc.perform(get("/files/" + id + "/download"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(dl.getResponse().getContentType()).isEqualTo("image/png");
        assertThat(dl.getResponse().getContentAsByteArray()).isEqualTo(PNG);

        // delete -> gone
        mockMvc.perform(delete("/files/" + id)).andExpect(status().isOk());
        getJson("/files/" + id, 404);
    }

    @Test
    @WithMockUser
    void upload_deniedExtension_isRejected() throws Exception {
        JsonNode body = upload("malware.exe", "application/octet-stream", new byte[]{'M', 'Z', 0, 1}, 415);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_EXTENSION_NOT_ALLOWED");
    }

    @Test
    @WithMockUser
    void upload_contentDoesNotMatchExtension_isRejected() throws Exception {
        // PNG bytes disguised as a .pdf -> magic-byte check catches the mismatch.
        JsonNode body = upload("document.pdf", "application/pdf", PNG, 415);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_CONTENT_MISMATCH");
    }

    @Test
    @WithMockUser
    void upload_emptyFile_isRejected() throws Exception {
        JsonNode body = upload("empty.png", "image/png", new byte[0], 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_EMPTY");
    }

    @Test
    @WithMockUser
    void upload_toExplicitActiveBackend_succeeds() throws Exception {
        JsonNode data = mockMvcUpload("pic.png", PNG, "FILESYSTEM", 201).path("data");
        assertThat(data.path("storageType").asText()).isEqualTo("FILESYSTEM");
    }

    @Test
    @WithMockUser
    void upload_toInactiveBackend_isRejected() throws Exception {
        // S3 isn't configured in the test profile -> selecting it is a 400.
        JsonNode body = mockMvcUpload("pic.png", PNG, "S3", 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("STORAGE_BACKEND_NOT_ACTIVE");
    }

    @Test
    @WithMockUser
    void storageBackends_listsActiveAndDefault() throws Exception {
        JsonNode data = getJson("/files/storage-backends", 200).path("data");
        assertThat(data.path("active").toString()).contains("FILESYSTEM");
        assertThat(data.path("defaultType").asText()).isEqualTo("FILESYSTEM");
    }

    @Test
    void download_isPublic_reachesControllerWithoutAuth() throws Exception {
        // No @WithMockUser: a public endpoint reaches the controller (404 for a missing id), not 401.
        mockMvc.perform(get("/files/" + java.util.UUID.randomUUID() + "/download"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/files/" + java.util.UUID.randomUUID() + "/thumbnail/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void upload_withoutAuth_isUnauthorized() throws Exception {
        mockMvc.perform(multipart("/files").file(new MockMultipartFile("file", "x.png", "image/png", PNG)))
                .andExpect(status().isUnauthorized());
    }

    private JsonNode mockMvcUpload(String filename, byte[] bytes, String storageType, int expectedStatus) throws Exception {
        var builder = multipart("/files").file(new MockMultipartFile("file", filename, "image/png", bytes));
        builder.param("storageType", storageType);
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().is(expectedStatus))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return body.isBlank() ? MAPPER.createObjectNode() : MAPPER.readTree(body);
    }

    private JsonNode upload(String filename, String contentType, byte[] bytes, int expectedStatus) throws Exception {
        MockMultipartFile part = new MockMultipartFile("file", filename, contentType, bytes);
        MvcResult result = mockMvc.perform(multipart("/files").file(part))
                .andExpect(status().is(expectedStatus))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return body.isBlank() ? MAPPER.createObjectNode() : MAPPER.readTree(body);
    }
}
