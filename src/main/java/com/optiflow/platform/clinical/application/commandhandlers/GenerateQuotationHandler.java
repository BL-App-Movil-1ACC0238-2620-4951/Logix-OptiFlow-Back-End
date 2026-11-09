package com.optiflow.platform.clinical.application.commandhandlers;

import com.optiflow.platform.clinical.domain.exceptions.ClinicalRecordNotFoundException;
import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.commands.GenerateQuotationCommand;
import com.optiflow.platform.clinical.domain.model.entities.QuotationItem;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenerateQuotationHandler {

  private final ClinicalRecordRepository clinicalRecordRepository;
  private final QuotationRepository quotationRepository;

  public GenerateQuotationHandler(
      ClinicalRecordRepository clinicalRecordRepository, QuotationRepository quotationRepository) {
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.quotationRepository = quotationRepository;
  }

  @Transactional
  public Quotation handle(GenerateQuotationCommand command) {
    ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(command.clinicalRecordId())
        .orElseThrow(ClinicalRecordNotFoundException::new);
    if (!clinicalRecord.hasPrescription()) {
      throw new DomainException(
          "The clinical record has no optical prescription. Register one first.", 409);
    }
    List<QuotationItem> items = command.items().stream()
        .map(item -> QuotationItem.create(
            item.itemType(),
            item.productSku(),
            item.description(),
            item.unitPrice(),
            item.quantity()))
        .toList();
    Quotation quotation = Quotation.generate(clinicalRecord.id(), items);
    quotationRepository.save(quotation);
    return quotation;
  }
}
