package com.gucardev.springreactboilerplate.domain.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseMockMvcTest;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Coverage of the image upload pipeline: a PNG is re-encoded to optimized WebP, a thumbnail is
 * generated and stored (downloadable), the url endpoint exposes a thumbnail URL, and non-images are
 * rejected. Uses Scrimage's bundled WebP encoder.
 */
class ImageIT extends BaseMockMvcTest {

    // A real, fully decodable PNG (Scrimage decodes it, unlike a 1x1 stub).
    private static final byte[] PNG = pngBytes();

    private static byte[] pngBytes() {
        try {
            BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, 32, 32);
            g.dispose();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    @WithMockUser
    void uploadImage_optimizesToWebp_andStoresThumbnail() throws Exception {
        JsonNode data = uploadImage("photo.png", PNG, 201).path("data");
        String id = data.path("id").asText();

        // optimized to WebP
        assertThat(data.path("contentType").asText()).isEqualTo("image/webp");
        assertThat(data.path("extension").asText()).isEqualTo(".webp");
        assertThat(data.path("originalFilename").asText()).isEqualTo("photo.webp");
        assertThat(data.path("storageType").asText()).isEqualTo("FILESYSTEM");

        // url endpoint exposes both the file and the thumbnail
        JsonNode url = getJson("/files/" + id + "/url", 200).path("data");
        assertThat(url.path("url").asText()).isEqualTo("/files/" + id + "/download");
        assertThat(url.path("thumbnailUrl").asText()).isEqualTo("/files/" + id + "/thumbnail/download");

        // both main and thumbnail are downloadable as WebP
        MvcResult main = mockMvc.perform(get("/files/" + id + "/download"))
                .andExpect(status().isOk()).andReturn();
        assertThat(main.getResponse().getContentType()).isEqualTo("image/webp");
        assertThat(main.getResponse().getContentAsByteArray()).isNotEmpty();

        MvcResult thumb = mockMvc.perform(get("/files/" + id + "/thumbnail/download"))
                .andExpect(status().isOk()).andReturn();
        assertThat(thumb.getResponse().getContentType()).isEqualTo("image/webp");
        assertThat(thumb.getResponse().getContentAsByteArray()).isNotEmpty();
    }

    @Test
    @WithMockUser
    void uploadImage_withNonImage_isRejected() throws Exception {
        JsonNode body = uploadImage("notes.txt", "hello world, this is plain text".getBytes(), 415);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_NOT_AN_IMAGE");
    }

    private JsonNode uploadImage(String filename, byte[] bytes, int expectedStatus) throws Exception {
        MockMultipartFile part = new MockMultipartFile("file", filename, "application/octet-stream", bytes);
        MvcResult result = mockMvc.perform(multipart("/files/images").file(part))
                .andExpect(status().is(expectedStatus))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return body.isBlank() ? MAPPER.createObjectNode() : MAPPER.readTree(body);
    }
}
