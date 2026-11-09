package com.optiflow.platform.clinical.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

/** Product of a closed sale, as Store Management & Inventory needs it to update the stock. */
public record SoldProduct(String productSku, int quantity) {

  public SoldProduct {
    if (productSku == null || productSku.isBlank()) {
      throw new DomainException("Product SKU is required.", 400);
    }
    if (quantity <= 0) {
      throw new DomainException("Sold quantity must be greater than zero.", 400);
    }
  }
}
