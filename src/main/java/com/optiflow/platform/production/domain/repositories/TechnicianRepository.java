package com.optiflow.platform.production.domain.repositories;

import com.optiflow.platform.production.domain.model.entities.Technician;
import com.optiflow.platform.production.domain.model.valueobjects.TechnicianId;
import java.util.List;
import java.util.Optional;

public interface TechnicianRepository {

  void save(Technician technician);

  Optional<Technician> findById(TechnicianId id);

  List<Technician> findAll();

  boolean isEmpty();
}
