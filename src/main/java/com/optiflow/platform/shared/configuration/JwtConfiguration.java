package com.optiflow.platform.shared.configuration;

import com.optiflow.platform.shared.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {}
