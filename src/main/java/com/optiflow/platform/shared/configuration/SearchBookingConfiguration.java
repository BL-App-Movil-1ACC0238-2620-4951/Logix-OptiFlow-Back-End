package com.optiflow.platform.shared.configuration;

import com.optiflow.platform.searchbooking.domain.services.AppointmentAvailabilityService;
import com.optiflow.platform.searchbooking.domain.services.AppointmentFactory;
import com.optiflow.platform.searchbooking.domain.services.OpticalStoreSearchService;
import com.optiflow.platform.searchbooking.domain.services.PatientFactory;
import com.optiflow.platform.searchbooking.domain.services.StoreRatingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchBookingConfiguration {

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
