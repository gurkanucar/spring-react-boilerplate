package com.gucardev.springreactboilerplate.features.core.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

/**
 * Coverage of the image upload pipeline: a PNG is re-encoded to optimized WebP, a thumbnail is
 * generated and stored (downloadable), the url endpoint exposes a thumbnail URL, and non-images are
 * rejected. Uses Scrimage's bundled WebP encoder.
 */
class ImageIT extends BaseIntegrationTest {

    // A real, fully decodable PNG (Scrimage decodes it, unlike a 1x1 stub).
    private static final byte[] PNG = pngBytes();

    @Test
    @WithMockUser
    void uploadImage_optimizesToWebp_andStoresThumbnail() {
        JsonNode data = uploadMultipart("/files/images", PNG, "photo.png", MediaType.IMAGE_PNG, null, 201).path("data");
        String id = data.path("id").asText();

        assertThat(data.path("contentType").asText()).isEqualTo("image/webp");
        assertThat(data.path("extension").asText()).isEqualTo(".webp");
        assertThat(data.path("originalFilename").asText()).isEqualTo("photo.webp");
        assertThat(data.path("storageType").asText()).isEqualTo("FILESYSTEM");

        JsonNode url = getJson("/files/" + id + "/url", 200).path("data");
        assertThat(url.path("url").asText()).isEqualTo("/files/" + id + "/download");
        assertThat(url.path("thumbnailUrl").asText()).isEqualTo("/files/" + id + "/thumbnail/download");

        EntityExchangeResult<byte[]> main = client.get().uri("/files/" + id + "/download")
                .exchange().expectStatus().isOk().expectBody(byte[].class).returnResult();
        assertThat(main.getResponseHeaders().getContentType()).hasToString("image/webp");
        assertThat(main.getResponseBody()).isNotEmpty();

        EntityExchangeResult<byte[]> thumb = client.get().uri("/files/" + id + "/thumbnail/download")
                .exchange().expectStatus().isOk().expectBody(byte[].class).returnResult();
        assertThat(thumb.getResponseHeaders().getContentType()).hasToString("image/webp");
        assertThat(thumb.getResponseBody()).isNotEmpty();
    }

    @Test
    @WithMockUser
    void uploadImage_withNonImage_isRejected() {
        JsonNode body = uploadMultipart("/files/images", "hello world, this is plain text".getBytes(),
                "notes.txt", MediaType.TEXT_PLAIN, null, 415);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("FILE_NOT_AN_IMAGE");
    }

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
}
