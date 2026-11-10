package com.optiflow.platform.notification.infrastructure.outboundservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.notification.infrastructure.configuration.FirebaseProperties;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class FirebaseMessagingAdapterTest {

  private static final String BASE_URL = "https://fcm.googleapis.com";
  private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
  private static final String SEND_URL = BASE_URL + "/v1/projects/optiflow-test/messages:send";
  private static final FcmMessage MESSAGE = new FcmMessage(
      "Avance de tu pedido", "Tu pedido está listo para recoger en la óptica.",
      Map.of("type", "LENS_ORDER_PROGRESS"));

  private final RestClient.Builder restClientBuilder = RestClient.builder();
  private final MockRestServiceServer server =
      MockRestServiceServer.bindTo(restClientBuilder).build();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void sendsWithOneAccessTokenAndReportsInvalidDeviceTokens() throws Exception {
    server.expect(once(), requestTo(TOKEN_URI))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().string(Matchers.containsString("grant-type%3Ajwt-bearer")))
        .andExpect(request -> assertEquals(TOKEN_URI,
            assertionClaims(((MockClientHttpRequest) request).getBodyAsString()).path("aud")
                .asText()))
        .andRespond(withSuccess(
            "{\"access_token\":\"ya29.test\",\"expires_in\":3599}",
            MediaType.APPLICATION_JSON));
    server.expect(requestTo(SEND_URL))
        .andExpect(header("Authorization", "Bearer ya29.test"))
        .andExpect(jsonPath("$.message.token").value("device-1"))
        .andExpect(jsonPath("$.message.notification.title").value("Avance de tu pedido"))
        .andExpect(jsonPath("$.message.data.type").value("LENS_ORDER_PROGRESS"))
        .andRespond(withSuccess("{\"name\":\"projects/optiflow-test/messages/1\"}",
            MediaType.APPLICATION_JSON));
    server.expect(requestTo(SEND_URL))
        .andExpect(jsonPath("$.message.token").value("device-2"))
        .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
            .body("{\"error\":{\"status\":\"NOT_FOUND\",\"message\":\"UNREGISTERED\"}}"));

    FirebaseMessagingAdapter adapter = adapter(credentials());

    assertEquals(FcmSendResult.SENT, adapter.send("device-1", MESSAGE));
    assertEquals(FcmSendResult.INVALID_TOKEN, adapter.send("device-2", MESSAGE));
    server.verify();
  }

  @Test
  void sendsNothingWithoutCredentials() {
    FirebaseMessagingAdapter adapter = adapter("");

    assertFalse(adapter.isConfigured());
    assertEquals(FcmSendResult.FAILED, adapter.send("device-1", MESSAGE));
  }

  private FirebaseMessagingAdapter adapter(String credentials) {
    return new FirebaseMessagingAdapter(new FirebaseProperties(credentials, BASE_URL),
        restClientBuilder, objectMapper,
        Clock.fixed(Instant.parse("2026-11-08T15:00:00Z"), ZoneOffset.UTC));
  }

  /** Claims of the JWT sent to Google; its audience must be a single string, not a list. */
  private JsonNode assertionClaims(String form) throws java.io.IOException {
    String assertion = URLDecoder.decode(
        form.replaceAll(".*assertion=([^&]+).*", "$1"), StandardCharsets.UTF_8);
    return objectMapper.readTree(Base64.getUrlDecoder().decode(assertion.split("\\.")[1]));
  }

  /** Service account key in the format Firebase downloads, with a key made for the test. */
  private String credentials() throws Exception {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    byte[] key = generator.generateKeyPair().getPrivate().getEncoded();
    String pem = "-----BEGIN PRIVATE KEY-----\n" + Base64.getMimeEncoder().encodeToString(key)
        + "\n-----END PRIVATE KEY-----\n";
    return objectMapper.writeValueAsString(Map.of(
        "type", "service_account",
        "project_id", "optiflow-test",
        "client_email", "firebase-adminsdk@optiflow-test.iam.gserviceaccount.com",
        "private_key", pem,
        "token_uri", TOKEN_URI));
  }
}
