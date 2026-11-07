package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationItemType;
import java.util.List;

public record GenerateQuotationCommand(ClinicalRecordId clinicalRecordId, List<Item> items) {

  public record Item(
      QuotationItemType itemType,
      String productSku,
      String description,
      Money unitPrice,
      int quantity) {
  }
}
