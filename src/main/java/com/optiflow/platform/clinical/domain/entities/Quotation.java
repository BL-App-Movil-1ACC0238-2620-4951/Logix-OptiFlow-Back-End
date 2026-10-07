package com.optiflow.platform.clinical.domain.entities;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.Discount;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationStatus;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Quotation {

  private static final int MAX_ITEMS = 20;

  private final QuotationId id;
  private final ClinicalRecordId clinicalRecordId;
  private final List<QuotationItem> items;
  private Discount discount;
  private Money total;
  private QuotationStatus status;
  private String rejectionReason;

  private Quotation(
      QuotationId id,
      ClinicalRecordId clinicalRecordId,
      List<QuotationItem> items,
      Discount discount,
      Money total,
      QuotationStatus status,
      String rejectionReason) {
    this.id = id;
    this.clinicalRecordId = clinicalRecordId;
    this.items = new ArrayList<>(items);
    this.discount = discount;
    this.total = total;
    this.status = status;
    this.rejectionReason = rejectionReason;
  }

  public static Quotation generate(ClinicalRecordId clinicalRecordId, List<QuotationItem> items) {
    if (items == null || items.isEmpty()) {
      throw new DomainException("A quotation needs at least one item.", 400);
    }
    Quotation quotation = new Quotation(
        QuotationId.generate(), clinicalRecordId, List.of(), null, Money.ZERO,
        QuotationStatus.DRAFT, null);
    items.forEach(quotation::addItem);
    return quotation;
  }

  public static Quotation reconstitute(
      QuotationId id,
      ClinicalRecordId clinicalRecordId,
      List<QuotationItem> items,
      Discount discount,
      Money total,
      QuotationStatus status,
      String rejectionReason) {
    return new Quotation(id, clinicalRecordId, items, discount, total, status, rejectionReason);
  }

  public void addItem(QuotationItem item) {
    requireDraft();
    if (items.size() >= MAX_ITEMS) {
      throw new DomainException("A quotation can have at most 20 items.", 400);
    }
    items.add(item);
    total = calculateTotal();
  }

  public void applyPromotionOrDiscount(Discount discount) {
    requireDraft();
    discount.amountFor(subtotal());
    this.discount = discount;
    total = calculateTotal();
  }

  public void approve() {
    requireDraft();
    status = QuotationStatus.APPROVED;
  }

  public void reject(String reason) {
    requireDraft();
    if (reason == null || reason.isBlank()) {
      throw new DomainException("A rejection reason is required.", 400);
    }
    if (reason.trim().length() > 255) {
      throw new DomainException("Rejection reason must have at most 255 characters.", 400);
    }
    rejectionReason = reason.trim();
    status = QuotationStatus.REJECTED;
  }

  public Money subtotal() {
    return items.stream().map(QuotationItem::subtotal).reduce(Money.ZERO, Money::add);
  }

  public Money discountAmount() {
    return discount == null ? Money.ZERO : discount.amountFor(subtotal());
  }

  public Money calculateTotal() {
    return subtotal().subtract(discountAmount());
  }

  public boolean isApproved() {
    return status == QuotationStatus.APPROVED;
  }

  private void requireDraft() {
    if (status != QuotationStatus.DRAFT) {
      throw new DomainException("Only a draft quotation can be modified.", 409);
    }
  }

  public QuotationId id() {
    return id;
  }

  public ClinicalRecordId clinicalRecordId() {
    return clinicalRecordId;
  }

  public List<QuotationItem> items() {
    return List.copyOf(items);
  }

  public Optional<Discount> discount() {
    return Optional.ofNullable(discount);
  }

  public Money total() {
    return total;
  }

  public QuotationStatus status() {
    return status;
  }

  public String rejectionReason() {
    return rejectionReason;
  }
}
