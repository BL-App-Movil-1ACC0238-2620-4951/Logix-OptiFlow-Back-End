package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.QuotationEntity;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.mappers.QuotationMapper;
import java.util.List;
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
    return quotationJpaRepository.findById(id.value()).map(this::toDomain);
  }

  @Override
  public List<Quotation> findByClinicalRecordIds(List<ClinicalRecordId> clinicalRecordIds) {
    if (clinicalRecordIds.isEmpty()) {
      return List.of();
    }
    return quotationJpaRepository.findByClinicalRecordIdIn(
            clinicalRecordIds.stream().map(ClinicalRecordId::value).toList()).stream()
        .map(this::toDomain)
        .toList();
  }

  private Quotation toDomain(QuotationEntity entity) {
    return quotationMapper.toDomain(
        entity, quotationItemJpaRepository.findByQuotationIdOrderByLineNumber(entity.getId()));
  }
}
