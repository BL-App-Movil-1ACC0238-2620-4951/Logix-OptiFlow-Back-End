package com.optiflow.booking.domain.repository;

import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OpticalStoreRepository {

  Optional<OpticalStore> findById(OpticalStoreId id);

  List<OpticalStore> search(String name, String address);

  List<OpticalStore> filter(String name, String address, BigDecimal minRating);

  void save(OpticalStore opticalStore);

  boolean isEmpty();
}
