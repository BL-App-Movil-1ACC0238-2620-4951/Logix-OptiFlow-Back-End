package com.optiflow.platform.clinical;

import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.entities.QuotationItem;
import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationItemType;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Saves an approved quotation of S/ 489.00 and its sale pending payment. */
public final class SaleTestData {

  public static final BigDecimal TOTAL = new BigDecimal("489.00");

  private SaleTestData() {
  }

  public static UUID pendingSale(
      QuotationRepository quotationRepository, SaleRepository saleRepository, UUID patientId) {
    Quotation quotation = Quotation.generate(ClinicalRecordId.generate(), List.of(
        QuotationItem.create(QuotationItemType.FRAME, "RB-2140-BLK", "Montura Ray-Ban Wayfarer",
            Money.of(new BigDecimal("320.00")), 1),
        QuotationItem.create(QuotationItemType.LENS, null, "Lunas policarbonato",
            Money.of(new BigDecimal("169.00")), 1)));
    quotation.approve();
    quotationRepository.save(quotation);
    Sale sale = Sale.register(quotation.id(), PatientId.of(patientId));
    saleRepository.save(sale);
    return sale.id().value();
  }
}
