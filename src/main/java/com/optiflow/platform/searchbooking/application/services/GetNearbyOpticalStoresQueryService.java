package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.queries.GetNearbyOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.valueobjects.GeoLocation;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.Comparator;
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
    GeoLocation reference = new GeoLocation(query.latitude(), query.longitude());
    double maxDistance = query.radiusKm().doubleValue();
    return opticalStoreRepository.findAll().stream()
        .filter(OpticalStore::isActive)
        .filter(store -> store.location().isPresent())
        .filter(store -> store.location().orElseThrow().distanceKmTo(reference) <= maxDistance)
        .sorted(Comparator.comparingDouble(
            store -> store.location().map(loc -> loc.distanceKmTo(reference)).orElse(Double.MAX_VALUE)))
        .toList();
  }
}
