package com.optiflow.booking.interfaces.rest.assembler;

import com.optiflow.booking.application.query.SearchOpticalStoresQuery;
import org.springframework.stereotype.Component;

@Component
public class FromSearchOpticalStoreRequestAssembler {

  public SearchOpticalStoresQuery toQuery(String name, String address) {
    return new SearchOpticalStoresQuery(name, address);
  }
}
