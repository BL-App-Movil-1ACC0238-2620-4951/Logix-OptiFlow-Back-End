package com.optiflow.platform.clinical.application.queryservices;

import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.queries.GetQuotationByIdQuery;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuotationQueryService {

  private final QuotationRepository quotationRepository;

  public QuotationQueryService(QuotationRepository quotationRepository) {
    this.quotationRepository = quotationRepository;
  }

  @Transactional(readOnly = true)
  public Quotation handle(GetQuotationByIdQuery query) {
    return quotationRepository.findById(query.quotationId())
        .orElseThrow(QuotationNotFoundException::new);
  }
}
