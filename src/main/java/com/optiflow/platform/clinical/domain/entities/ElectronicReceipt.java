package com.optiflow.platform.clinical.domain.entities;

import com.optiflow.platform.clinical.domain.valueobjects.ElectronicReceiptId;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class ElectronicReceipt {

  /** Sale prices already include IGV (18%), so the tax is extracted from the total. */
  private static final BigDecimal IGV_FACTOR = new BigDecimal("1.18");

  private final ElectronicReceiptId id;
  private final String receiptNumber;
  private final SaleId saleId;
  private final Instant issueDate;
  private final Money taxAmount;
  private final Money totalAmount;

  private ElectronicReceipt(
      ElectronicReceiptId id,
      String receiptNumber,
      SaleId saleId,
      Instant issueDate,
      Money taxAmount,
      Money totalAmount) {
    this.id = id;
    this.receiptNumber = receiptNumber;
    this.saleId = saleId;
    this.issueDate = issueDate;
    this.taxAmount = taxAmount;
    this.totalAmount = totalAmount;
  }

  public static ElectronicReceipt issue(
      SaleId saleId, String receiptNumber, Money totalAmount, Instant now) {
    BigDecimal taxBase = totalAmount.amount().divide(IGV_FACTOR, 2, RoundingMode.HALF_UP);
    Money taxAmount = Money.of(totalAmount.amount().subtract(taxBase));
    return new ElectronicReceipt(
        ElectronicReceiptId.generate(), receiptNumber, saleId, now, taxAmount, totalAmount);
  }

  public static ElectronicReceipt reconstitute(
      ElectronicReceiptId id,
      String receiptNumber,
      SaleId saleId,
      Instant issueDate,
      Money taxAmount,
      Money totalAmount) {
    return new ElectronicReceipt(id, receiptNumber, saleId, issueDate, taxAmount, totalAmount);
  }

  public static String formatNumber(long sequence) {
    return "B001-%08d".formatted(sequence);
  }

  public ElectronicReceiptId id() {
    return id;
  }

  public String receiptNumber() {
    return receiptNumber;
  }

  public SaleId saleId() {
    return saleId;
  }

  public Instant issueDate() {
    return issueDate;
  }

  public Money taxAmount() {
    return taxAmount;
  }

  public Money totalAmount() {
    return totalAmount;
  }
}
