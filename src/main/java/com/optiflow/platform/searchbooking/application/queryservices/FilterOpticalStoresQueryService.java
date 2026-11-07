package com.optiflow.platform.searchbooking.application.queryservices;

import com.optiflow.platform.searchbooking.domain.model.queries.FilterOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FilterOpticalStoresQueryService {

  private final OpticalStoreRepository opticalStoreRepository;

  public FilterOpticalStoresQueryService(OpticalStoreRepository opticalStoreRepository) {
    this.opticalStoreRepository = opticalStoreRepository;
  }

  @Transactional(readOnly = true)
  public List<OpticalStore> handle(FilterOpticalStoresQuery query) {
    return opticalStoreRepository.filter(query.name(), query.address(), query.minRating());
  }
}
