package com.optiflow.platform.shared.infrastructure.configuration;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

/** API messages are always in English, whatever language the client asks for. */
@Configuration
public class LocaleConfiguration {

  @Bean
  LocaleResolver localeResolver() {
    return new FixedLocaleResolver(Locale.ENGLISH);
  }
}
