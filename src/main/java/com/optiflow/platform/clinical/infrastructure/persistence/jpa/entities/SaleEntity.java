package com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sales")
public class SaleEntity {

  @Id
  private UUID id;

  @Column(name = "quotation_id", nullable = false, unique = true)
  private UUID quotationId;

  @Column(name = "patient_id", nullable = false)
  private UUID patientId;

  @Column(nullable = false, length = 30)
  private String status;

  @Column(name = "closed_at")
  private Instant closedAt;

  @Column(name = "payment_method", length = 30)
  private String paymentMethod;

  @Column(name = "payment_amount", precision = 10, scale = 2)
  private BigDecimal paymentAmount;

  @Column(name = "transaction_reference", length = 255)
  private String transactionReference;

  @Column(name = "paid_at")
  private Instant paidAt;

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

  public UUID getPatientId() {
    return patientId;
  }

  public void setPatientId(UUID patientId) {
    this.patientId = patientId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant getClosedAt() {
    return closedAt;
  }

  public void setClosedAt(Instant closedAt) {
    this.closedAt = closedAt;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public BigDecimal getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(BigDecimal paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public String getTransactionReference() {
    return transactionReference;
  }

  public void setTransactionReference(String transactionReference) {
    this.transactionReference = transactionReference;
  }

  public Instant getPaidAt() {
    return paidAt;
  }

  public void setPaidAt(Instant paidAt) {
    this.paidAt = paidAt;
  }
}
