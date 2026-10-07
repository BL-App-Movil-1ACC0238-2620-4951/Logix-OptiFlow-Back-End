package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.entities.ElectronicReceipt;
import com.optiflow.platform.clinical.domain.entities.MedicalHistory;
import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.entities.QuotationItem;
import com.optiflow.platform.clinical.domain.entities.Sale;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.OpticalPrescription;
import com.optiflow.platform.clinical.domain.valueobjects.Payment;
import com.optiflow.platform.clinical.interfaces.rest.resources.ClinicalRecordResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.DiscountResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.ElectronicReceiptResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.MedicalHistoryResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.OpticalPrescriptionResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.PaymentResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.QuotationItemResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.QuotationResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.SaleResponse;
import org.springframework.stereotype.Component;

@Component
public class ClinicalCommercialResponseAssembler {

  public ClinicalRecordResponse toClinicalRecordResponse(ClinicalRecord clinicalRecord) {
    return new ClinicalRecordResponse(
        clinicalRecord.id().value(),
        clinicalRecord.patientId().value(),
        clinicalRecord.appointmentId().value(),
        clinicalRecord.examinationDate(),
        clinicalRecord.observations(),
        clinicalRecord.status().name(),
        clinicalRecord.medicalHistory().map(this::toMedicalHistoryResponse).orElse(null),
        clinicalRecord.prescription()
            .map(prescription -> toPrescriptionResponse(clinicalRecord, prescription))
            .orElse(null));
  }

  public OpticalPrescriptionResponse toPrescriptionResponse(ClinicalRecord clinicalRecord) {
    return clinicalRecord.prescription()
        .map(prescription -> toPrescriptionResponse(clinicalRecord, prescription))
        .orElse(null);
  }

  public QuotationResponse toQuotationResponse(Quotation quotation) {
    DiscountResponse discount = quotation.discount()
        .map(value -> new DiscountResponse(
            value.type().name(),
            value.value(),
            value.reason(),
            quotation.discountAmount().amount()))
        .orElse(null);
    return new QuotationResponse(
        quotation.id().value(),
        quotation.clinicalRecordId().value(),
        quotation.items().stream().map(this::toItemResponse).toList(),
        discount,
        quotation.subtotal().amount(),
        quotation.total().amount(),
        Money.CURRENCY,
        quotation.status().name(),
        quotation.rejectionReason());
  }

  public SaleResponse toSaleResponse(Sale sale) {
    return new SaleResponse(
        sale.id().value(),
        sale.quotationId().value(),
        sale.patientId().value(),
        sale.status().name(),
        sale.closedAt(),
        sale.payment().map(this::toPaymentResponse).orElse(null),
        sale.receipt().map(this::toReceiptResponse).orElse(null));
  }

  private MedicalHistoryResponse toMedicalHistoryResponse(MedicalHistory medicalHistory) {
    return new MedicalHistoryResponse(
        medicalHistory.allergies(),
        medicalHistory.previousConditions(),
        medicalHistory.familyOcularHistory(),
        medicalHistory.lastUpdated());
  }

  private OpticalPrescriptionResponse toPrescriptionResponse(
      ClinicalRecord clinicalRecord, OpticalPrescription prescription) {
    return new OpticalPrescriptionResponse(
        clinicalRecord.id().value(),
        prescription.sphereOd(),
        prescription.cylinderOd(),
        prescription.axisOd(),
        prescription.sphereOs(),
        prescription.cylinderOs(),
        prescription.axisOs(),
        prescription.addition(),
        prescription.treatment(),
        prescription.recommendedFrameType());
  }

  private QuotationItemResponse toItemResponse(QuotationItem item) {
    return new QuotationItemResponse(
        item.id().value(),
        item.itemType().name(),
        item.productSku(),
        item.description(),
        item.unitPrice().amount(),
        item.quantity(),
        item.subtotal().amount());
  }

  private PaymentResponse toPaymentResponse(Payment payment) {
    return new PaymentResponse(
        payment.method().name(),
        payment.amount().amount(),
        payment.transactionReference(),
        payment.paidAt());
  }

  private ElectronicReceiptResponse toReceiptResponse(ElectronicReceipt receipt) {
    return new ElectronicReceiptResponse(
        receipt.id().value(),
        receipt.receiptNumber(),
        receipt.issueDate(),
        receipt.taxAmount().amount(),
        receipt.totalAmount().amount());
  }
}
