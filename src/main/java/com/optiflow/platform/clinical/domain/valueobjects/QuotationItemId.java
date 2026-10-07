package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record QuotationItemId(UUID value) {

  public QuotationItemId {
    if (value == null) {
      throw new DomainException("Quotation item id is required.", 400);
    }
  }

  public static QuotationItemId generate() {
    return new QuotationItemId(UUID.randomUUID());
  }

  public static QuotationItemId of(UUID value) {
    return new QuotationItemId(value);
  }
}
