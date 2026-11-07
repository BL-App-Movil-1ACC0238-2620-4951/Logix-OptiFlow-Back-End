package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.application.queries.FilterOpticalStoresQuery;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreQueryParams;
import org.springframework.stereotype.Component;

@Component
public class FromOpticalStoreQueryParamsAssembler {

  public FilterOpticalStoresQuery toQuery(OpticalStoreQueryParams params) {
    return new FilterOpticalStoresQuery(params.name(), params.address(), params.minRating());
  }
}
