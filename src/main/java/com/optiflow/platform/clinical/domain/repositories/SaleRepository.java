package com.optiflow.platform.clinical.domain.repositories;

import com.optiflow.platform.clinical.domain.entities.Sale;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
import java.util.Optional;

public interface SaleRepository {

  void save(Sale sale);

  Optional<Sale> findById(SaleId id);

  boolean existsByQuotationId(QuotationId quotationId);

  long countIssuedReceipts();
}
