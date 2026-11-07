package com.optiflow.platform.clinical.application.results;

import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleStatus;
import java.util.UUID;

/**
 * Result of {@code GetClosedSale}: the sale with the quotation and clinical record it comes from
 * and the optical store where the patient was attended.
 *
 * @param opticalStoreId store of the appointment; {@code null} when it cannot be found
 */
public record ClosedSaleDetails(
    Sale sale, Quotation quotation, ClinicalRecord clinicalRecord, UUID opticalStoreId) {

  public boolean closed() {
    return sale.status() == SaleStatus.CLOSED;
  }
}
