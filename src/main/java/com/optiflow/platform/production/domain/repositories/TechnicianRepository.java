package com.optiflow.platform.production.domain.repositories;

import com.optiflow.platform.production.domain.entities.Technician;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import java.util.List;
import java.util.Optional;

public interface TechnicianRepository {

  void save(Technician technician);

  Optional<Technician> findById(TechnicianId id);

  List<Technician> findAll();

  boolean isEmpty();
}
