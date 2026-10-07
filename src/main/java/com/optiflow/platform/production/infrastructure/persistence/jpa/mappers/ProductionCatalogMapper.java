package com.optiflow.platform.production.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.production.domain.entities.Laboratory;
import com.optiflow.platform.production.domain.entities.Technician;
import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.LaboratoryEntity;
import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.TechnicianEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductionCatalogMapper {

  public Technician toDomain(TechnicianEntity entity) {
    return new Technician(TechnicianId.of(entity.getId()), entity.getName());
  }

  public TechnicianEntity toEntity(Technician technician) {
    TechnicianEntity entity = new TechnicianEntity();
    entity.setId(technician.id().value());
    entity.setName(technician.name());
    return entity;
  }

  public Laboratory toDomain(LaboratoryEntity entity) {
    return new Laboratory(LaboratoryId.of(entity.getId()), entity.getName());
  }

  public LaboratoryEntity toEntity(Laboratory laboratory) {
    LaboratoryEntity entity = new LaboratoryEntity();
    entity.setId(laboratory.id().value());
    entity.setName(laboratory.name());
    return entity;
  }
}
