package com.optiflow.booking.infrastructure.config;

import com.optiflow.booking.domain.factory.AppointmentFactory;
import com.optiflow.booking.domain.factory.PatientFactory;
import com.optiflow.booking.domain.service.AppointmentAvailabilityService;
import com.optiflow.booking.domain.service.OpticalStoreSearchService;
import com.optiflow.booking.domain.service.StoreRatingService;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchBookingContextConfig {

  @Bean
  Clock clock() {
    return Clock.system(ZoneId.of("America/Lima"));
  }

  @Bean
  AppointmentAvailabilityService appointmentAvailabilityService() {
    return new AppointmentAvailabilityService();
  }

  @Bean
  OpticalStoreSearchService opticalStoreSearchService() {
    return new OpticalStoreSearchService();
  }

  @Bean
  StoreRatingService storeRatingService() {
    return new StoreRatingService();
  }

  @Bean
  PatientFactory patientFactory() {
    return new PatientFactory();
  }

  @Bean
  AppointmentFactory appointmentFactory() {
    return new AppointmentFactory();
  }
}
