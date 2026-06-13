package com.gucardev.springreactboilerplate.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Setting and removing the current user's profile image. Runs as the seeded admin (real user loaded
 * via {@code @WithUserDetails}). Verifies the user references the uploaded (optimized) image by id,
 * that {@code /auth/me} reflects it, and that removal clears it.
 */
@WithUserDetails("admin@mail.com")
class ProfileImageIT extends BaseIntegrationTest {

    private static final byte[] PNG = pngBytes();

    @Test
    void setProfileImage_thenRemove() throws Exception {
        // set -> user now references the uploaded image
        MvcResult setResult = mockMvc.perform(multipart("/auth/me/profile-image")
                        .file(new MockMultipartFile("file", "avatar.png", "image/png", PNG)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode setData = MAPPER.readTree(setResult.getResponse().getContentAsString()).path("data");
        String imageId = setData.path("profileImageId").asText();
        assertThat(imageId).isNotBlank();
        // resolved URLs are returned right away (filesystem -> app download endpoints)
        assertThat(setData.path("profileImageUrl").asText()).isEqualTo("/files/" + imageId + "/download");
        assertThat(setData.path("profileImageThumbnailUrl").asText())
                .isEqualTo("/files/" + imageId + "/thumbnail/download");

        // /auth/me reflects it (id + resolved url), and the underlying file exists
        JsonNode me = getJson("/auth/me", 200).path("data");
        assertThat(me.path("profileImageId").asText()).isEqualTo(imageId);
        assertThat(me.path("profileImageUrl").asText()).isEqualTo("/files/" + imageId + "/download");
        getJson("/files/" + imageId, 200);

        // remove -> cleared
        MvcResult removeResult = mockMvc.perform(delete("/auth/me/profile-image"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode removeData = MAPPER.readTree(removeResult.getResponse().getContentAsString()).path("data");
        assertThat(removeData.hasNonNull("profileImageId")).isFalse();
    }

    @Test
    void profileImageUrl_isResolvedInUserList_viaBatch() throws Exception {
        // admin sets a profile image, then lists users (admin role) — the list row carries the url
        mockMvc.perform(multipart("/auth/me/profile-image")
                        .file(new MockMultipartFile("file", "a.png", "image/png", PNG)))
                .andExpect(status().isOk());

        JsonNode data = getJson("/api/v1/users?email=admin@mail.com", 200).path("data");
        assertThat(data.size()).isGreaterThan(0);
        JsonNode admin = data.get(0);
        assertThat(admin.path("email").asText()).isEqualTo("admin@mail.com");
        assertThat(admin.path("profileImageUrl").asText()).startsWith("/files/");
    }

    private static byte[] pngBytes() {
        try {
            BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.BLUE);
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
