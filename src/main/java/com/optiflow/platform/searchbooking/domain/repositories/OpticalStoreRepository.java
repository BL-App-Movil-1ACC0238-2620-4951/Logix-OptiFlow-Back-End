package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.model.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OpticalStoreRepository {

  Optional<OpticalStore> findById(OpticalStoreId id);

  List<OpticalStore> search(String name, String address);

  List<OpticalStore> filter(String name, String address, BigDecimal minRating);

  void save(OpticalStore opticalStore);

  List<OpticalStore> findAll();

  boolean isEmpty();
}
