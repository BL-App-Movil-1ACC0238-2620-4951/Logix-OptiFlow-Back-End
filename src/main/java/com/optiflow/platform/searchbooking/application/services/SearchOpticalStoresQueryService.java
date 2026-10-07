package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.queries.SearchOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchOpticalStoresQueryService {

  private final OpticalStoreRepository opticalStoreRepository;

  public SearchOpticalStoresQueryService(OpticalStoreRepository opticalStoreRepository) {
    this.opticalStoreRepository = opticalStoreRepository;
  }

  @Transactional(readOnly = true)
  public List<OpticalStore> handle(SearchOpticalStoresQuery query) {
    return opticalStoreRepository.search(query.name(), query.address());
  }
}
