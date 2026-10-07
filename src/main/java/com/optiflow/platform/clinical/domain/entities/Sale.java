package com.optiflow.platform.clinical.domain.entities;

import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.valueobjects.Payment;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
import com.optiflow.platform.clinical.domain.valueobjects.SaleStatus;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.util.Optional;

public class Sale {

  private final SaleId id;
  private final QuotationId quotationId;
  private final PatientId patientId;
  private SaleStatus status;
  private Instant closedAt;
  private Payment payment;
  private ElectronicReceipt receipt;

  private Sale(
      SaleId id,
      QuotationId quotationId,
      PatientId patientId,
      SaleStatus status,
      Instant closedAt,
      Payment payment,
      ElectronicReceipt receipt) {
    this.id = id;
    this.quotationId = quotationId;
    this.patientId = patientId;
    this.status = status;
    this.closedAt = closedAt;
    this.payment = payment;
    this.receipt = receipt;
  }

  public static Sale register(QuotationId quotationId, PatientId patientId) {
    return new Sale(
        SaleId.generate(), quotationId, patientId, SaleStatus.PENDING_PAYMENT, null, null, null);
  }

  public static Sale reconstitute(
      SaleId id,
      QuotationId quotationId,
      PatientId patientId,
      SaleStatus status,
      Instant closedAt,
      Payment payment,
      ElectronicReceipt receipt) {
    return new Sale(id, quotationId, patientId, status, closedAt, payment, receipt);
  }

  /** Validates a payment before it is sent to the payment gateway. */
  public void ensurePaymentCanBeRecorded(Money amount, Money amountDue) {
    if (status != SaleStatus.PENDING_PAYMENT) {
      throw new DomainException("A payment was already recorded for this sale.", 409);
    }
    if (amount == null || amount.amount().compareTo(amountDue.amount()) != 0) {
      throw new DomainException(
          "The payment amount must match the quotation total of " + amountDue + ".", 400);
    }
  }

  public void recordPayment(Payment payment, Money amountDue) {
    ensurePaymentCanBeRecorded(payment.amount(), amountDue);
    this.payment = payment;
    status = SaleStatus.PAID;
  }

  public ElectronicReceipt close(String receiptNumber, Instant now) {
    if (status != SaleStatus.PAID) {
      throw new DomainException("Only a paid sale can be closed.", 409);
    }
    receipt = ElectronicReceipt.issue(id, receiptNumber, payment.amount(), now);
    closedAt = now;
    status = SaleStatus.CLOSED;
    return receipt;
  }

  public SaleId id() {
    return id;
  }

  public QuotationId quotationId() {
    return quotationId;
  }

  public PatientId patientId() {
    return patientId;
  }

  public SaleStatus status() {
    return status;
  }

  public Instant closedAt() {
    return closedAt;
  }

  public Optional<Payment> payment() {
    return Optional.ofNullable(payment);
  }

  public Optional<ElectronicReceipt> receipt() {
    return Optional.ofNullable(receipt);
  }
}
