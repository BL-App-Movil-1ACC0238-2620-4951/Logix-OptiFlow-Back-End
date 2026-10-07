package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.application.queries.FilterOpticalStoresQuery;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class FromFilterOpticalStoreRequestAssembler {

  public FilterOpticalStoresQuery toQuery(String name, String address, BigDecimal minRating) {
    return new FilterOpticalStoresQuery(name, address, minRating);
  }
}
