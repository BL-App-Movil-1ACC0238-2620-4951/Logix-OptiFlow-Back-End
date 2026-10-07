package com.optiflow.platform.clinical.domain.repositories;

import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import java.util.Optional;

public interface QuotationRepository {

  void save(Quotation quotation);

  Optional<Quotation> findById(QuotationId id);
}
