package com.optiflow.platform.clinical.domain.repositories;

import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SaleRepository {

  void save(Sale sale);

  Optional<Sale> findById(SaleId id);

  List<Sale> findByPatientId(PatientId patientId);

  boolean existsByQuotationId(QuotationId quotationId);

  long countIssuedReceipts();

  /** Sales closed from {@code from} (inclusive) to {@code to} (exclusive). */
  List<Sale> findClosedBetween(Instant from, Instant to);
}
