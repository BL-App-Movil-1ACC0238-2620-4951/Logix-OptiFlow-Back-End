package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.domain.model.queries.FilterOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetNearbyOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.OpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.SearchOpticalStoresQuery;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreQueryParams;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import org.springframework.stereotype.Component;

@Component
public class FromOpticalStoreQueryParamsAssembler {

  /**
   * Location parameters give a nearby search, a minimum rating gives a filter and name or
   * address alone (or no parameters) give a text search.
   */
  public OpticalStoresQuery toQuery(OpticalStoreQueryParams params) {
    if (params.isNearbySearch()) {
      if (params.lat() == null || params.lng() == null || params.radiusKm() == null) {
        throw new DomainException("lat, lng and radiusKm are required for a nearby search.", 400);
      }
      return new GetNearbyOpticalStoresQuery(params.lat(), params.lng(), params.radiusKm());
    }
    if (params.minRating() != null) {
      return new FilterOpticalStoresQuery(params.name(), params.address(), params.minRating());
    }
    return new SearchOpticalStoresQuery(params.name(), params.address());
  }
}
