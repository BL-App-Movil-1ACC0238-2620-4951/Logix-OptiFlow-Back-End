package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.RegisterSaleCommand;
import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.entities.Sale;
import com.optiflow.platform.clinical.domain.exceptions.ClinicalRecordNotFoundException;
import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.shared.exceptions.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterSaleHandler {

  private final QuotationRepository quotationRepository;
  private final ClinicalRecordRepository clinicalRecordRepository;
  private final SaleRepository saleRepository;

  public RegisterSaleHandler(
      QuotationRepository quotationRepository,
      ClinicalRecordRepository clinicalRecordRepository,
      SaleRepository saleRepository) {
    this.quotationRepository = quotationRepository;
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.saleRepository = saleRepository;
  }

  @Transactional
  public Sale handle(RegisterSaleCommand command) {
    Quotation quotation = quotationRepository.findById(command.quotationId())
        .orElseThrow(QuotationNotFoundException::new);
    if (!quotation.isApproved()) {
      throw new DomainException("Only an approved quotation can generate a sale.", 409);
    }
    if (saleRepository.existsByQuotationId(quotation.id())) {
      throw new DomainException("This quotation already has a sale.", 409);
    }
    ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(quotation.clinicalRecordId())
        .orElseThrow(ClinicalRecordNotFoundException::new);
    Sale sale = Sale.register(quotation.id(), clinicalRecord.patientId());
    saleRepository.save(sale);
    return sale;
  }
}
