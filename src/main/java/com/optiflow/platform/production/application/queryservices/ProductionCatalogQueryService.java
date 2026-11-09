package com.optiflow.platform.production.application.queryservices;

import com.optiflow.platform.production.domain.model.entities.Laboratory;
import com.optiflow.platform.production.domain.model.entities.Technician;
import com.optiflow.platform.production.domain.model.queries.GetLaboratoriesQuery;
import com.optiflow.platform.production.domain.model.queries.GetTechniciansQuery;
import com.optiflow.platform.production.domain.repositories.LaboratoryRepository;
import com.optiflow.platform.production.domain.repositories.TechnicianRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read access to the technicians and laboratories that can work on an order. */
@Service
public class ProductionCatalogQueryService {

  private final TechnicianRepository technicianRepository;
  private final LaboratoryRepository laboratoryRepository;

  public ProductionCatalogQueryService(
      TechnicianRepository technicianRepository, LaboratoryRepository laboratoryRepository) {
    this.technicianRepository = technicianRepository;
    this.laboratoryRepository = laboratoryRepository;
  }

  @Transactional(readOnly = true)
  public List<Technician> handle(GetTechniciansQuery query) {
    return technicianRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Laboratory> handle(GetLaboratoriesQuery query) {
    return laboratoryRepository.findAll();
  }
}
