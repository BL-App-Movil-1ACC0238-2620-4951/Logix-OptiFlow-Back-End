package com.optiflow.platform.production.application.outboundservices;

import com.optiflow.platform.production.domain.model.valueobjects.SaleId;
import java.util.Optional;

/** Outbound port to read sales from the Clinical & Commercial context. */
public interface ExternalSaleService {

  Optional<ClosedSale> findSale(SaleId saleId);
}
