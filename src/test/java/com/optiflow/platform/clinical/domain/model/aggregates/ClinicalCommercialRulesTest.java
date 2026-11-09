package com.optiflow.platform.clinical.domain.model.aggregates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.optiflow.platform.clinical.domain.model.entities.ElectronicReceipt;
import com.optiflow.platform.clinical.domain.model.entities.QuotationItem;
import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordStatus;
import com.optiflow.platform.clinical.domain.model.valueobjects.Discount;
import com.optiflow.platform.clinical.domain.model.valueobjects.DiscountType;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.OpticalPrescription;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Payment;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentStatus;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationItemType;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationStatus;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleStatus;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClinicalCommercialRulesTest {

  private static final Instant NOW = Instant.parse("2026-10-07T15:00:00Z");

  @Test
  void requiresAnExaminationBeforeGeneratingAPrescription() {
    ClinicalRecord clinicalRecord = ClinicalRecord.open(PatientId.generate(), AppointmentId.generate());

    DomainException exception = assertThrows(
        DomainException.class, () -> clinicalRecord.generatePrescription(prescription(90)));
    assertEquals(409, exception.status());

    clinicalRecord.registerExamination(NOW, "Visión borrosa de lejos");
    clinicalRecord.generatePrescription(prescription(90));

    assertEquals(ClinicalRecordStatus.OPEN, clinicalRecord.status());
    assertEquals(true, clinicalRecord.hasPrescription());
  }

  @Test
  void rejectsAnAxisOutsideTheZeroToOneHundredEightyRange() {
    DomainException exception = assertThrows(DomainException.class, () -> prescription(181));

    assertEquals(400, exception.status());
    assertEquals("Axis OD must be between 0 and 180 degrees.", exception.getMessage());
  }

  @Test
  void calculatesTheQuotationTotalWithAPercentageDiscount() {
    Quotation quotation = draftQuotation();

    quotation.applyPromotionOrDiscount(
        new Discount(DiscountType.PERCENTAGE, new BigDecimal("10"), "Campaña escolar"));

    assertEquals(money("489.00"), quotation.subtotal());
    assertEquals(money("48.90"), quotation.discountAmount());
    assertEquals(money("440.10"), quotation.total());
  }

  @Test
  void rejectsADiscountGreaterThanTheSubtotal() {
    Quotation quotation = draftQuotation();
    Discount discount = new Discount(DiscountType.FIXED_AMOUNT, new BigDecimal("500"), null);

    DomainException exception = assertThrows(
        DomainException.class, () -> quotation.applyPromotionOrDiscount(discount));
    assertEquals(400, exception.status());
  }

  @Test
  void onlyADraftQuotationCanBeApprovedOrRejected() {
    Quotation quotation = draftQuotation();
    quotation.approve();

    assertEquals(QuotationStatus.APPROVED, quotation.status());
    DomainException exception =
        assertThrows(DomainException.class, () -> quotation.reject("Muy caro"));
    assertEquals(409, exception.status());
  }

  @Test
  void requiresTheExactAmountAndIssuesTheReceiptWithIgv() {
    Sale sale = Sale.register(draftQuotation().id(), PatientId.generate());
    Money amountDue = money("440.10");

    DomainException exception = assertThrows(DomainException.class, () -> sale.recordPayment(
        Payment.approved(PaymentMethod.CASH, money("400.00"), "CASH-1", NOW), amountDue));
    assertEquals(400, exception.status());

    sale.recordPayment(Payment.approved(PaymentMethod.YAPE, amountDue, "YAPE-123", NOW), amountDue);
    ElectronicReceipt receipt = sale.close(ElectronicReceipt.formatNumber(1), NOW);

    assertEquals(SaleStatus.CLOSED, sale.status());
    assertEquals("B001-00000001", receipt.receiptNumber());
    assertEquals(money("440.10"), receipt.totalAmount());
    assertEquals(money("67.13"), receipt.taxAmount());
  }

  @Test
  void keepsTheSalePendingWhenThePaymentIsRejected() {
    Sale sale = Sale.register(draftQuotation().id(), PatientId.generate());
    Money amountDue = money("440.10");
    Payment rejected = new Payment(PaymentMethod.CARD, amountDue, "111", PaymentStatus.REJECTED,
        "cc_rejected_insufficient_amount", NOW);

    assertEquals(400, assertThrows(DomainException.class,
        () -> sale.recordPayment(rejected, amountDue)).status());
    sale.rejectPayment(rejected);
    assertEquals(SaleStatus.PENDING_PAYMENT, sale.status());
    assertTrue(sale.hasPayment("111"));

    sale.recordPayment(Payment.approved(PaymentMethod.YAPE, amountDue, "222", NOW), amountDue);
    assertEquals(SaleStatus.PAID, sale.status());
    assertEquals(409, assertThrows(DomainException.class,
        () -> sale.rejectPayment(rejected)).status());
  }

  private static Quotation draftQuotation() {
    return Quotation.generate(ClinicalRecordId.generate(), List.of(
        QuotationItem.create(
            QuotationItemType.FRAME, "RB-2140-BLK", "Montura Ray-Ban Wayfarer",
            money("320.00"), 1),
        QuotationItem.create(
            QuotationItemType.LENS, null, "Lunas policarbonato con antirreflejo",
            money("169.00"), 1)));
  }

  private static OpticalPrescription prescription(int axisOd) {
    return new OpticalPrescription(
        new BigDecimal("-1.25"), new BigDecimal("-1.00"),
        new BigDecimal("-0.50"), new BigDecimal("-0.75"),
        axisOd, 85, null, "Antirreflejo", null);
  }

  private static Money money(String amount) {
    return Money.of(new BigDecimal(amount));
  }
}
