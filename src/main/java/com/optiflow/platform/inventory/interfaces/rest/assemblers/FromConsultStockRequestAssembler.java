package com.optiflow.platform.inventory.interfaces.rest.assemblers;

import com.optiflow.platform.inventory.domain.model.queries.ConsultStockQuery;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.interfaces.rest.resources.ConsultStockRequest;
import org.springframework.stereotype.Component;

@Component
public class FromConsultStockRequestAssembler {

  public ConsultStockQuery toQuery(ConsultStockRequest request) {
    return new ConsultStockQuery(OpticalStoreId.of(request.opticalStoreId()));
  }
}
