package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.ApproveQuotationCommand;
import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.events.QuotationApproved;
import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApproveQuotationHandler {

  private final QuotationRepository quotationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public ApproveQuotationHandler(
      QuotationRepository quotationRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.quotationRepository = quotationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Quotation handle(ApproveQuotationCommand command) {
    Quotation quotation = quotationRepository.findById(command.quotationId())
        .orElseThrow(QuotationNotFoundException::new);
    quotation.approve();
    quotationRepository.save(quotation);
    eventPublisher.publish(new QuotationApproved(
        quotation.id(), quotation.clinicalRecordId(), quotation.total(), clock.instant()));
    return quotation;
  }
}
