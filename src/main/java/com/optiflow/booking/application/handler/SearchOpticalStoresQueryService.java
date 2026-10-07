package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.query.SearchOpticalStoresQuery;
import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.repository.OpticalStoreRepository;
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
