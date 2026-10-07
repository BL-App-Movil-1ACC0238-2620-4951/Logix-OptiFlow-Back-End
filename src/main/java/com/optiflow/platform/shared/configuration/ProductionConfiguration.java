package com.optiflow.platform.shared.configuration;

import com.optiflow.platform.production.domain.entities.Laboratory;
import com.optiflow.platform.production.domain.entities.Technician;
import com.optiflow.platform.production.domain.repositories.LaboratoryRepository;
import com.optiflow.platform.production.domain.repositories.TechnicianRepository;
import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import java.util.UUID;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring configuration for the Production & Tracking bounded context. */
@Configuration
public class ProductionConfiguration {

  static final UUID JORGE_SALAS_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
  static final UUID MARIA_QUISPE_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
  static final UUID CENTRAL_LAB_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
  static final UUID EXPRESS_LAB_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");

  /** Loads the technicians and laboratories that can work on an order, once. */
  @Bean
  ApplicationRunner productionCatalogSeeder(
      TechnicianRepository technicianRepository, LaboratoryRepository laboratoryRepository) {
    return args -> {
      if (technicianRepository.isEmpty()) {
        technicianRepository.save(new Technician(TechnicianId.of(JORGE_SALAS_ID), "Jorge Salas"));
        technicianRepository.save(
            new Technician(TechnicianId.of(MARIA_QUISPE_ID), "María Quispe"));
      }
      if (laboratoryRepository.isEmpty()) {
        laboratoryRepository.save(
            new Laboratory(LaboratoryId.of(CENTRAL_LAB_ID), "Laboratorio Central OptiFlow"));
        laboratoryRepository.save(
            new Laboratory(LaboratoryId.of(EXPRESS_LAB_ID), "Laboratorio Visión Express"));
      }
    };
  }
}
