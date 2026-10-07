package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.queries.GetOpticalStoreQuery;
import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.shared.exceptions.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetOpticalStoreQueryService {

  private final OpticalStoreRepository opticalStoreRepository;

  public GetOpticalStoreQueryService(OpticalStoreRepository opticalStoreRepository) {
    this.opticalStoreRepository = opticalStoreRepository;
  }

  @Transactional(readOnly = true)
  public OpticalStore handle(GetOpticalStoreQuery query) {
    return opticalStoreRepository.findById(query.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
  }
}
