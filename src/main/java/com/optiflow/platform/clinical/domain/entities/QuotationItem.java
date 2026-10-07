package com.optiflow.platform.clinical.domain.entities;

import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationItemId;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationItemType;
import com.optiflow.platform.shared.exceptions.DomainException;

public class QuotationItem {

  private static final int MAX_QUANTITY = 100;

  private final QuotationItemId id;
  private final QuotationItemType itemType;
  private final String productSku;
  private final String description;
  private final Money unitPrice;
  private final int quantity;

  private QuotationItem(
      QuotationItemId id,
      QuotationItemType itemType,
      String productSku,
      String description,
      Money unitPrice,
      int quantity) {
    this.id = id;
    this.itemType = itemType;
    this.productSku = productSku;
    this.description = description;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
  }

  public static QuotationItem create(
      QuotationItemType itemType,
      String productSku,
      String description,
      Money unitPrice,
      int quantity) {
    if (itemType == null) {
      throw new DomainException("Item type is required.", 400);
    }
    if (description == null || description.isBlank()) {
      throw new DomainException("Item description is required.", 400);
    }
    if (description.trim().length() > 255) {
      throw new DomainException("Item description must have at most 255 characters.", 400);
    }
    String sku = productSku == null || productSku.isBlank() ? null : productSku.trim();
    if (sku != null && sku.length() > 60) {
      throw new DomainException("Product SKU must have at most 60 characters.", 400);
    }
    if (unitPrice == null) {
      throw new DomainException("Item unit price is required.", 400);
    }
    if (quantity < 1 || quantity > MAX_QUANTITY) {
      throw new DomainException("Item quantity must be between 1 and 100.", 400);
    }
    return new QuotationItem(
        QuotationItemId.generate(), itemType, sku, description.trim(), unitPrice, quantity);
  }

  public static QuotationItem reconstitute(
      QuotationItemId id,
      QuotationItemType itemType,
      String productSku,
      String description,
      Money unitPrice,
      int quantity) {
    return new QuotationItem(id, itemType, productSku, description, unitPrice, quantity);
  }

  public Money subtotal() {
    return unitPrice.multiply(quantity);
  }

  public QuotationItemId id() {
    return id;
  }

  public QuotationItemType itemType() {
    return itemType;
  }

  public String productSku() {
    return productSku;
  }

  public String description() {
    return description;
  }

  public Money unitPrice() {
    return unitPrice;
  }

  public int quantity() {
    return quantity;
  }
}
