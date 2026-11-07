package com.optiflow.platform.searchbooking.application.queryservices;

import com.optiflow.platform.searchbooking.domain.model.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.model.queries.GetNearbyOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.GeoLocation;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNearbyOpticalStoresQueryService {

  private final OpticalStoreRepository opticalStoreRepository;

  public GetNearbyOpticalStoresQueryService(OpticalStoreRepository opticalStoreRepository) {
    this.opticalStoreRepository = opticalStoreRepository;
  }

  @Transactional(readOnly = true)
  public List<OpticalStore> handle(GetNearbyOpticalStoresQuery query) {
    if (query.radiusKm() == null || query.radiusKm().signum() <= 0) {
      throw new DomainException("radiusKm must be greater than zero.", 400);
    }
    return opticalStoreRepository.findNearby(
        new GeoLocation(query.latitude(), query.longitude()), query.radiusKm());
  }
}
