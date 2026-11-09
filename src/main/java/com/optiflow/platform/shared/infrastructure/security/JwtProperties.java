package com.optiflow.platform.shared.infrastructure.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "optiflow.security.jwt")
public record JwtProperties(String secret, Duration expiration) {}
