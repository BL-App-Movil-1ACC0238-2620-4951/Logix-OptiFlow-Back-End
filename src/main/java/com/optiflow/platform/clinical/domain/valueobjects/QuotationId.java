package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record QuotationId(UUID value) {

  public QuotationId {
    if (value == null) {
      throw new DomainException("Quotation id is required.", 400);
    }
  }

  public static QuotationId generate() {
    return new QuotationId(UUID.randomUUID());
  }

  public static QuotationId of(UUID value) {
    return new QuotationId(value);
  }
}
