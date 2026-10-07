package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationItemType;
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
