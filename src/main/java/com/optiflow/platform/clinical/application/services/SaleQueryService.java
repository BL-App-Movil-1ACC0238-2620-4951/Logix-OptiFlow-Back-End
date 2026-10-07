package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.queries.GetSaleByIdQuery;
import com.optiflow.platform.clinical.domain.entities.Sale;
import com.optiflow.platform.clinical.domain.exceptions.SaleNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleQueryService {

  private final SaleRepository saleRepository;

  public SaleQueryService(SaleRepository saleRepository) {
    this.saleRepository = saleRepository;
  }

  @Transactional(readOnly = true)
  public Sale handle(GetSaleByIdQuery query) {
    return saleRepository.findById(query.saleId()).orElseThrow(SaleNotFoundException::new);
  }
}
