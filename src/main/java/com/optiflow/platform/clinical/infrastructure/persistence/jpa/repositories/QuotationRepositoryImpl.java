package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.mappers.QuotationMapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class QuotationRepositoryImpl implements QuotationRepository {

  private final QuotationJpaRepository quotationJpaRepository;
  private final QuotationItemJpaRepository quotationItemJpaRepository;
  private final QuotationMapper quotationMapper;

  public QuotationRepositoryImpl(
      QuotationJpaRepository quotationJpaRepository,
      QuotationItemJpaRepository quotationItemJpaRepository,
      QuotationMapper quotationMapper) {
    this.quotationJpaRepository = quotationJpaRepository;
    this.quotationItemJpaRepository = quotationItemJpaRepository;
    this.quotationMapper = quotationMapper;
  }

  @Override
  public void save(Quotation quotation) {
    quotationJpaRepository.save(quotationMapper.toEntity(quotation));
    quotationItemJpaRepository.saveAll(quotationMapper.toItemEntities(quotation));
  }

  @Override
  public Optional<Quotation> findById(QuotationId id) {
    return quotationJpaRepository.findById(id.value())
        .map(entity -> quotationMapper.toDomain(
            entity, quotationItemJpaRepository.findByQuotationIdOrderByLineNumber(entity.getId())));
  }
}
