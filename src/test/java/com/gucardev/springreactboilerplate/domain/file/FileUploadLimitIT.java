package com.gucardev.springreactboilerplate.domain.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseMockMvcTest;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Verifies the {@code upload.max-bytes} cap. Sets a tiny limit so a normal small file trips it
 * with the domain FILE_TOO_LARGE error (not the raw container limit).
 */
@TestPropertySource(properties = "upload.max-bytes=8")
class FileUploadLimitIT extends BaseMockMvcTest {

    private static final byte[] PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+M8AAAMBAQDJ/pLvAAAAAElFTkSuQmCC");

    @Test
    @WithMockUser
    void upload_overMaxBytes_isRejected() throws Exception {
        MockMultipartFile part = new MockMultipartFile("file", "photo.png", "image/png", PNG);
        MvcResult result = mockMvc.perform(multipart("/files").file(part))
                .andExpect(status().is(413))
                .andReturn();
        JsonNode body = MAPPER.readTree(result.getResponse().getContentAsString());
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_TOO_LARGE");
    }
}
