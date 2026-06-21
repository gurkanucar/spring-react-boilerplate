package com.gucardev.springreactboilerplate.features.core.user;

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
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Setting and removing the current user's profile image. Runs as the seeded admin (real user loaded
 * via {@code @WithUserDetails}). Verifies the user references the uploaded (optimized) image by id,
 * that the resolved URLs come back, that {@code /auth/me} and the user list reflect it, and that
 * removal clears it.
 */
@WithUserDetails("admin@mail.com")
class ProfileImageIT extends BaseIntegrationTest {

    private static final byte[] PNG = pngBytes();

    @Test
    void setProfileImage_thenRemove() {
        JsonNode setData = uploadMultipart("/auth/me/profile-image", PNG, "avatar.png",
                MediaType.IMAGE_PNG, null, 200).path("data");
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
        JsonNode removeData = deleteJson("/auth/me/profile-image", 200).path("data");
        assertThat(removeData.hasNonNull("profileImageId")).isFalse();
    }

    @Test
    void profileImageUrl_isResolvedInUserList_viaBatch() {
        uploadMultipart("/auth/me/profile-image", PNG, "a.png", MediaType.IMAGE_PNG, null, 200);

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
