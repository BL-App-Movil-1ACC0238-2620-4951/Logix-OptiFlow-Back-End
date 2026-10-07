package com.optiflow.platform.production.domain.repositories;

import com.optiflow.platform.production.domain.entities.Laboratory;
import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import java.util.List;
import java.util.Optional;

public interface LaboratoryRepository {

  void save(Laboratory laboratory);

  Optional<Laboratory> findById(LaboratoryId id);

  List<Laboratory> findAll();

  boolean isEmpty();
}
