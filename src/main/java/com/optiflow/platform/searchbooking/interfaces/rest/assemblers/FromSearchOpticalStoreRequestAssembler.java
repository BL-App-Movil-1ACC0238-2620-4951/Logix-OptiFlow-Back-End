package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.application.queries.SearchOpticalStoresQuery;
import org.springframework.stereotype.Component;

@Component
public class FromSearchOpticalStoreRequestAssembler {

  public SearchOpticalStoresQuery toQuery(String name, String address) {
    return new SearchOpticalStoresQuery(name, address);
  }
}
