package com.optiflow.platform.shared.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtTokenServiceTest {

  private static final String SECRET = "optiflow-test-jwt-secret-key-32chars!";
  private static final UUID USER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

  @Test
  void issuesAndParsesAPatientToken() {
    Clock clock = Clock.fixed(Instant.parse("2026-11-07T12:00:00Z"), ZoneOffset.UTC);
    JwtTokenService service = new JwtTokenService(
        new JwtProperties(SECRET, Duration.ofHours(8)), clock);

    IssuedJwt issued = service.issue(USER_ID, "PATIENT");

    assertEquals(Instant.parse("2026-11-07T20:00:00Z"), issued.expiresAt());
    JwtClaims claims = service.parse(issued.token());
    assertEquals(USER_ID, claims.userId());
    assertEquals("PATIENT", claims.role());
    assertEquals(issued.expiresAt(), claims.expiresAt());
  }

  @Test
  void rejectsAnExpiredToken() {
    Clock clock = Clock.fixed(Instant.parse("2026-11-07T12:00:00Z"), ZoneOffset.UTC);
    JwtTokenService service = new JwtTokenService(
        new JwtProperties(SECRET, Duration.ofMinutes(5)), clock);
    IssuedJwt issued = service.issue(USER_ID, "PATIENT");

    Clock later = Clock.fixed(Instant.parse("2026-11-07T13:00:00Z"), ZoneOffset.UTC);
    JwtTokenService validator =
        new JwtTokenService(new JwtProperties(SECRET, Duration.ofMinutes(5)), later);

    assertThrows(DomainException.class, () -> validator.parse(issued.token()));
  }
}
