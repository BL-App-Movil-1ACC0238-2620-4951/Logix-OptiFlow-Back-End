package com.optiflow.platform.clinical.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.clinical.domain.model.entities.ElectronicReceipt;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.valueobjects.ElectronicReceiptId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Payment;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleStatus;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.ElectronicReceiptEntity;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.SaleEntity;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

  public Sale toDomain(SaleEntity entity, ElectronicReceiptEntity receiptEntity) {
    Payment payment = entity.getPaymentMethod() == null
        ? null
        : new Payment(
            PaymentMethod.valueOf(entity.getPaymentMethod()),
            Money.of(entity.getPaymentAmount()),
            entity.getTransactionReference(),
            entity.getPaidAt());
    return Sale.reconstitute(
        SaleId.of(entity.getId()),
        QuotationId.of(entity.getQuotationId()),
        PatientId.of(entity.getPatientId()),
        SaleStatus.valueOf(entity.getStatus()),
        entity.getClosedAt(),
        payment,
        receiptEntity == null ? null : toReceipt(receiptEntity));
  }

  public SaleEntity toEntity(Sale sale) {
    SaleEntity entity = new SaleEntity();
    entity.setId(sale.id().value());
    entity.setQuotationId(sale.quotationId().value());
    entity.setPatientId(sale.patientId().value());
    entity.setStatus(sale.status().name());
    entity.setClosedAt(sale.closedAt());
    sale.payment().ifPresent(payment -> {
      entity.setPaymentMethod(payment.method().name());
      entity.setPaymentAmount(payment.amount().amount());
      entity.setTransactionReference(payment.transactionReference());
      entity.setPaidAt(payment.paidAt());
    });
    return entity;
  }

  public ElectronicReceiptEntity toReceiptEntity(ElectronicReceipt receipt) {
    ElectronicReceiptEntity entity = new ElectronicReceiptEntity();
    entity.setId(receipt.id().value());
    entity.setSaleId(receipt.saleId().value());
    entity.setReceiptNumber(receipt.receiptNumber());
    entity.setIssueDate(receipt.issueDate());
    entity.setTaxAmount(receipt.taxAmount().amount());
    entity.setTotalAmount(receipt.totalAmount().amount());
    return entity;
  }

  private ElectronicReceipt toReceipt(ElectronicReceiptEntity entity) {
    return ElectronicReceipt.reconstitute(
        ElectronicReceiptId.of(entity.getId()),
        entity.getReceiptNumber(),
        SaleId.of(entity.getSaleId()),
        entity.getIssueDate(),
        Money.of(entity.getTaxAmount()),
        Money.of(entity.getTotalAmount()));
  }
}
