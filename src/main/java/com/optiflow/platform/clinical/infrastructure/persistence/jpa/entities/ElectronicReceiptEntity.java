package com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "electronic_receipts")
public class ElectronicReceiptEntity {

  @Id
  private UUID id;

  @Column(name = "sale_id", nullable = false, unique = true)
  private UUID saleId;

  @Column(name = "receipt_number", nullable = false, unique = true, length = 100)
  private String receiptNumber;

  @Column(name = "issue_date", nullable = false)
  private Instant issueDate;

  @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal taxAmount;

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getSaleId() {
    return saleId;
  }

  public void setSaleId(UUID saleId) {
    this.saleId = saleId;
  }

  public String getReceiptNumber() {
    return receiptNumber;
  }

  public void setReceiptNumber(String receiptNumber) {
    this.receiptNumber = receiptNumber;
  }

  public Instant getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(Instant issueDate) {
    this.issueDate = issueDate;
  }

  public BigDecimal getTaxAmount() {
    return taxAmount;
  }

  public void setTaxAmount(BigDecimal taxAmount) {
    this.taxAmount = taxAmount;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }
}
