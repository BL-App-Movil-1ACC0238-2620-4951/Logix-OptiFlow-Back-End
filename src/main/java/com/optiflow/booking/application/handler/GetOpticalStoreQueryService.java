package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.query.GetOpticalStoreQuery;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.repository.OpticalStoreRepository;
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
