package com.optiflow.platform.clinical.domain.repositories;

import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import java.util.Optional;

public interface QuotationRepository {

  void save(Quotation quotation);

  Optional<Quotation> findById(QuotationId id);
}
