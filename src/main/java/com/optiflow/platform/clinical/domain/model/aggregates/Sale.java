package com.optiflow.platform.clinical.domain.model.aggregates;

import com.optiflow.platform.clinical.domain.model.entities.ElectronicReceipt;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Payment;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleStatus;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Instant;
import java.util.Optional;

public class Sale {

  private final SaleId id;
  private final QuotationId quotationId;
  private final PatientId patientId;
  private SaleStatus status;
  private Instant closedAt;
  private String paymentPreferenceId;
  private Payment payment;
  private ElectronicReceipt receipt;

  private Sale(
      SaleId id,
      QuotationId quotationId,
      PatientId patientId,
      SaleStatus status,
      Instant closedAt,
      String paymentPreferenceId,
      Payment payment,
      ElectronicReceipt receipt) {
    this.id = id;
    this.quotationId = quotationId;
    this.patientId = patientId;
    this.status = status;
    this.closedAt = closedAt;
    this.paymentPreferenceId = paymentPreferenceId;
    this.payment = payment;
    this.receipt = receipt;
  }

  public static Sale register(QuotationId quotationId, PatientId patientId) {
    return new Sale(
        SaleId.generate(), quotationId, patientId, SaleStatus.PENDING_PAYMENT, null, null, null,
        null);
  }

  public static Sale reconstitute(
      SaleId id,
      QuotationId quotationId,
      PatientId patientId,
      SaleStatus status,
      Instant closedAt,
      String paymentPreferenceId,
      Payment payment,
      ElectronicReceipt receipt) {
    return new Sale(
        id, quotationId, patientId, status, closedAt, paymentPreferenceId, payment, receipt);
  }

  public boolean belongsTo(PatientId patientId) {
    return this.patientId.equals(patientId);
  }

  public void ensurePendingPayment() {
    if (status != SaleStatus.PENDING_PAYMENT) {
      throw new DomainException("A payment was already recorded for this sale.", 409);
    }
  }

  /** Keeps the gateway preference that the patient pays from the app. */
  public void attachPaymentPreference(String preferenceId) {
    ensurePendingPayment();
    if (preferenceId == null || preferenceId.isBlank()) {
      throw new DomainException("Payment preference id is required.", 400);
    }
    paymentPreferenceId = preferenceId;
  }

  /** Validates a payment before it is recorded. */
  public void ensurePaymentCanBeRecorded(Money amount, Money amountDue) {
    ensurePendingPayment();
    if (amount == null || amount.amount().compareTo(amountDue.amount()) != 0) {
      throw new DomainException(
          "The payment amount must match the quotation total of " + amountDue + ".", 400);
    }
  }

  public void recordPayment(Payment payment, Money amountDue) {
    if (!payment.isApproved()) {
      throw new DomainException("Only an approved payment pays the sale.", 400);
    }
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

  public Optional<String> paymentPreferenceId() {
    return Optional.ofNullable(paymentPreferenceId);
  }

  public Optional<Payment> payment() {
    return Optional.ofNullable(payment);
  }

  public Optional<ElectronicReceipt> receipt() {
    return Optional.ofNullable(receipt);
  }
}
