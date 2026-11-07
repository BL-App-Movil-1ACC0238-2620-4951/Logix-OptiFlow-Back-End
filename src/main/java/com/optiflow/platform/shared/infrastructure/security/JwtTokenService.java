package com.optiflow.platform.shared.infrastructure.security;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

  private static final String ROLE_CLAIM = "role";

  private final JwtProperties properties;
  private final SecretKey secretKey;
  private final Clock clock;

  public JwtTokenService(JwtProperties properties, Clock clock) {
    if (properties.secret() == null || properties.secret().length() < 32) {
      throw new IllegalStateException("JWT secret must be at least 32 characters.");
    }
    this.properties = properties;
    this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    this.clock = clock;
  }

  public IssuedJwt issue(UUID userId, String role) {
    Instant now = clock.instant();
    Instant expiresAt = now.plus(properties.expiration());
    String token = Jwts.builder()
        .subject(userId.toString())
        .claim(ROLE_CLAIM, role)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(secretKey)
        .compact();
    return new IssuedJwt(token, expiresAt);
  }

  public JwtClaims parse(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(secretKey)
          .clock(() -> Date.from(clock.instant()))
          .build()
          .parseSignedClaims(token)
          .getPayload();
      UUID userId = UUID.fromString(claims.getSubject());
      String role = claims.get(ROLE_CLAIM, String.class);
      if (role == null || role.isBlank()) {
        throw new DomainException("The token is invalid.", 401);
      }
      Instant expiresAt = claims.getExpiration().toInstant();
      return new JwtClaims(userId, role, expiresAt);
    } catch (ExpiredJwtException exception) {
      throw new DomainException("The token has expired.", 401);
    } catch (JwtException | IllegalArgumentException exception) {
      throw new DomainException("The token is invalid.", 401);
    }
  }
}
