package com.optiflow.platform.clinical.infrastructure.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Spring configuration for the Clinical & Commercial bounded context. */
@Configuration
@EnableConfigurationProperties(MercadoPagoProperties.class)
public class ClinicalConfiguration {
}
