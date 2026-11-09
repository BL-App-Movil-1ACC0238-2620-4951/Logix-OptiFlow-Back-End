package com.optiflow.platform.searchbooking.domain.services;

import com.optiflow.platform.searchbooking.domain.model.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.GeoLocation;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreStatus;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class OpticalStoreSearchService {

  public List<OpticalStore> search(List<OpticalStore> stores, String name, String address) {
    return stores.stream()
        .filter(store -> store.status() == StoreStatus.ACTIVE)
        .filter(store -> contains(store.name().value(), name))
        .filter(store -> contains(store.address().value(), address))
        .toList();
  }

  public List<OpticalStore> filter(
      List<OpticalStore> stores, String name, String address, BigDecimal minRating) {
    return search(stores, name, address).stream()
        .filter(store -> minRating == null
            || store.rating().value().compareTo(minRating) >= 0)
        .toList();
  }

  /** Active stores within {@code radiusKm} of the reference point, nearest first. */
  public List<OpticalStore> nearby(
      List<OpticalStore> stores, GeoLocation reference, BigDecimal radiusKm) {
    double maxDistance = radiusKm.doubleValue();
    return stores.stream()
        .filter(store -> store.status() == StoreStatus.ACTIVE)
        .filter(store -> store.location().isPresent())
        .filter(store -> store.location().orElseThrow().distanceKmTo(reference) <= maxDistance)
        .sorted(Comparator.comparingDouble(
            store -> store.location().orElseThrow().distanceKmTo(reference)))
        .toList();
  }

  private boolean contains(String source, String expected) {
    if (expected == null || expected.isBlank()) {
      return true;
    }
    return source.toLowerCase(Locale.ROOT).contains(expected.trim().toLowerCase(Locale.ROOT));
  }
}
