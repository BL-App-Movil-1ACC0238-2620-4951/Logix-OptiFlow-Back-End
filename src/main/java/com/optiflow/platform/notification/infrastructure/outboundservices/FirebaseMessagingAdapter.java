package com.optiflow.platform.notification.infrastructure.outboundservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.notification.infrastructure.configuration.FirebaseProperties;
import io.jsonwebtoken.Jwts;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Sends messages through the HTTP v1 API of Firebase Cloud Messaging. It signs a JWT with the key
 * of the service account, exchanges it for an OAuth access token (kept until it expires) and posts
 * one message per device token.
 */
@Component
public class FirebaseMessagingAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseMessagingAdapter.class);
  private static final String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
  private static final String JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";
  private static final Duration TOKEN_LIFETIME = Duration.ofHours(1);
  private static final Duration TOKEN_MARGIN = Duration.ofMinutes(1);

  private final FirebaseProperties properties;
  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final Clock clock;
  private ServiceAccount serviceAccount;
  private String accessToken;
  private Instant accessTokenExpiresAt = Instant.MIN;

  public FirebaseMessagingAdapter(
      FirebaseProperties properties,
      RestClient.Builder restClientBuilder,
      ObjectMapper objectMapper,
      Clock clock) {
    this.properties = properties;
    this.restClient = restClientBuilder.build();
    this.objectMapper = objectMapper;
    this.clock = clock;
  }

  public boolean isConfigured() {
    return properties.credentials() != null && !properties.credentials().isBlank();
  }

  public FcmSendResult send(String deviceToken, FcmMessage message) {
    if (!isConfigured()) {
      return FcmSendResult.FAILED;
    }
    try {
      ServiceAccount account = serviceAccount();
      restClient.post()
          .uri(properties.baseUrl() + "/v1/projects/{projectId}/messages:send", account.projectId())
          .header("Authorization", "Bearer " + accessToken(account))
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of("message", Map.of(
              "token", deviceToken,
              "notification", Map.of("title", message.title(), "body", message.body()),
              "data", message.data(),
              "android", Map.of("priority", "high"))))
          .retrieve()
          .toBodilessEntity();
      return FcmSendResult.SENT;
    } catch (HttpClientErrorException exception) {
      if (isInvalidToken(exception)) {
        return FcmSendResult.INVALID_TOKEN;
      }
      LOGGER.warn("Firebase rejected a push notification: {}", exception.getStatusCode());
      return FcmSendResult.FAILED;
    } catch (RestClientException | IllegalStateException exception) {
      LOGGER.warn("Could not reach Firebase Cloud Messaging.", exception);
      return FcmSendResult.FAILED;
    }
  }

  /** FCM answers 404 UNREGISTERED or 400 INVALID_ARGUMENT for a token that is no longer valid. */
  private static boolean isInvalidToken(HttpClientErrorException exception) {
    String body = exception.getResponseBodyAsString();
    return exception.getStatusCode() == HttpStatus.NOT_FOUND
        || exception.getStatusCode() == HttpStatus.BAD_REQUEST
            && (body.contains("UNREGISTERED") || body.contains("INVALID_ARGUMENT"));
  }

  private synchronized String accessToken(ServiceAccount account) {
    Instant now = clock.instant();
    if (accessToken != null && now.isBefore(accessTokenExpiresAt.minus(TOKEN_MARGIN))) {
      return accessToken;
    }
    String assertion = Jwts.builder()
        .issuer(account.clientEmail())
        .claim("scope", SCOPE)
        .audience().add(account.tokenUri()).and()
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(TOKEN_LIFETIME)))
        .signWith(account.privateKey(), Jwts.SIG.RS256)
        .compact();
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", JWT_BEARER);
    form.add("assertion", assertion);
    JsonNode response = restClient.post()
        .uri(account.tokenUri())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(JsonNode.class);
    if (response == null || !response.hasNonNull("access_token")) {
      throw new IllegalStateException("Google did not return an access token.");
    }
    accessToken = response.get("access_token").asText();
    accessTokenExpiresAt = now.plusSeconds(response.path("expires_in").asLong(3600));
    return accessToken;
  }

  private synchronized ServiceAccount serviceAccount() {
    if (serviceAccount == null) {
      serviceAccount = ServiceAccount.parse(properties.credentials(), objectMapper);
    }
    return serviceAccount;
  }

  private record ServiceAccount(
      String projectId, String clientEmail, PrivateKey privateKey, String tokenUri) {

    static ServiceAccount parse(String credentials, ObjectMapper objectMapper) {
      try {
        JsonNode json = objectMapper.readTree(credentials);
        String pem = json.path("private_key").asText()
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        PrivateKey key = KeyFactory.getInstance("RSA")
            .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
        return new ServiceAccount(
            json.path("project_id").asText(),
            json.path("client_email").asText(),
            key,
            json.path("token_uri").asText("https://oauth2.googleapis.com/token"));
      } catch (java.io.IOException | GeneralSecurityException | IllegalArgumentException e) {
        throw new IllegalStateException("FIREBASE_CREDENTIALS is not a valid service account key.");
      }
    }
  }
}
