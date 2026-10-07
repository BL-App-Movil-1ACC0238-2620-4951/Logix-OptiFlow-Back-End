package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.ApplyPromotionOrDiscountCommand;
import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.events.PromotionOrDiscountApplied;
import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplyPromotionOrDiscountHandler {

  private final QuotationRepository quotationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public ApplyPromotionOrDiscountHandler(
      QuotationRepository quotationRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.quotationRepository = quotationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Quotation handle(ApplyPromotionOrDiscountCommand command) {
    Quotation quotation = quotationRepository.findById(command.quotationId())
        .orElseThrow(QuotationNotFoundException::new);
    quotation.applyPromotionOrDiscount(command.discount());
    quotationRepository.save(quotation);
    eventPublisher.publish(new PromotionOrDiscountApplied(
        quotation.id(), command.discount(), quotation.total(), clock.instant()));
    return quotation;
  }
}
