package com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "quotation_items")
public class QuotationItemEntity {

  @Id
  private UUID id;

  @Column(name = "quotation_id", nullable = false)
  private UUID quotationId;

  @Column(name = "line_number", nullable = false)
  private Integer lineNumber;

  @Column(name = "item_type", nullable = false, length = 30)
  private String itemType;

  @Column(name = "product_sku", length = 60)
  private String productSku;

  @Column(nullable = false, length = 255)
  private String description;

  @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  @Column(nullable = false)
  private Integer quantity;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getQuotationId() {
    return quotationId;
  }

  public void setQuotationId(UUID quotationId) {
    this.quotationId = quotationId;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(Integer lineNumber) {
    this.lineNumber = lineNumber;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public String getProductSku() {
    return productSku;
  }

  public void setProductSku(String productSku) {
    this.productSku = productSku;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}
