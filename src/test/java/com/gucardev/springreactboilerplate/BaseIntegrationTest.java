package com.gucardev.springreactboilerplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Single base for HTTP-level integration tests. Boots the full application context in a MOCK
 * servlet environment; the real Spring Security filter chain runs, requests execute on the test
 * thread (so {@code @WithMockUser}/{@code @WithUserDetails} work), and each test runs in a
 * transaction that rolls back.
 *
 * <p>Use {@link #client} ({@link RestTestClient}) for everything — JSON (the {@code postJson}/
 * {@code getJson}/{@code putJson}/{@code deleteJson} helpers take a request <em>object</em>), status
 * assertions and byte downloads. Multipart uploads are the one exception: {@code RestTestClient}
 * over MockMvc sends raw multipart bytes that {@code MockHttpServletRequest} can't parse, so file
 * uploads go through {@link MockMvc}'s {@code multipart()} builder via {@link #uploadMultipart}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Import(NoOpCacheConfig.class)
@Transactional
public abstract class BaseIntegrationTest {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    protected RestTestClient client;

    @Autowired
    protected MockMvc mockMvc;

    protected JsonNode postJson(String uri, Object body, int expectedStatus) {
        return parse(client.post().uri(uri).contentType(MediaType.APPLICATION_JSON).body(body)
                .exchange().expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    protected JsonNode putJson(String uri, Object body, int expectedStatus) {
        return parse(client.put().uri(uri).contentType(MediaType.APPLICATION_JSON).body(body)
                .exchange().expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    protected JsonNode getJson(String uri, int expectedStatus) {
        return parse(client.get().uri(uri)
                .exchange().expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    protected JsonNode deleteJson(String uri, int expectedStatus) {
        return parse(client.delete().uri(uri)
                .exchange().expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    /** Multipart upload of a single {@code file} part plus optional string params; returns parsed body. */
    protected JsonNode uploadMultipart(String uri, byte[] fileBytes, String filename, MediaType fileType,
                                       Map<String, String> params, int expectedStatus) {
        try {
            return parse(multipartUpload(uri, fileBytes, filename, fileType, params)
                    .andExpect(status().is(expectedStatus))
                    .andReturn().getResponse().getContentAsByteArray());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** Raw multipart upload, for callers asserting the status themselves. */
    protected ResultActions multipartUpload(String uri, byte[] fileBytes, String filename,
                                            MediaType fileType, Map<String, String> params) {
        try {
            MockMultipartHttpServletRequestBuilder builder = multipart(uri)
                    .file(new MockMultipartFile("file", filename, fileType.toString(), fileBytes));
            if (params != null) {
                params.forEach(builder::param);
            }
            return mockMvc.perform(builder);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private JsonNode parse(byte[] body) {
        try {
            return body == null || body.length == 0 ? MAPPER.createObjectNode() : MAPPER.readTree(body);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse JSON response body", e);
        }
    }
}
