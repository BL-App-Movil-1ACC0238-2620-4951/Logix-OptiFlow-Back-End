package com.optiflow.booking.interfaces.rest.assembler;

import com.optiflow.booking.application.query.FilterOpticalStoresQuery;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class FromFilterOpticalStoreRequestAssembler {

  public FilterOpticalStoresQuery toQuery(String name, String address, BigDecimal minRating) {
    return new FilterOpticalStoresQuery(name, address, minRating);
  }
}
