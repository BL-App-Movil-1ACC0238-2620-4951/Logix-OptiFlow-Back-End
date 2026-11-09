package com.optiflow.platform.inventory.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

/** Code of a frame model; it is the barcode that the scanner reads. */
public record Sku(String value) {

  public Sku {
    if (value == null || value.isBlank() || value.trim().length() > 100) {
      throw new DomainException("SKU is required (max. 100 characters).", 400);
    }
    value = value.trim();
  }

  public static Sku of(String value) {
    return new Sku(value);
  }
}
