package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.domain.entities.Sale;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.mappers.SaleMapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SaleRepositoryImpl implements SaleRepository {

  private final SaleJpaRepository saleJpaRepository;
  private final ElectronicReceiptJpaRepository electronicReceiptJpaRepository;
  private final SaleMapper saleMapper;

  public SaleRepositoryImpl(
      SaleJpaRepository saleJpaRepository,
      ElectronicReceiptJpaRepository electronicReceiptJpaRepository,
      SaleMapper saleMapper) {
    this.saleJpaRepository = saleJpaRepository;
    this.electronicReceiptJpaRepository = electronicReceiptJpaRepository;
    this.saleMapper = saleMapper;
  }

  @Override
  public void save(Sale sale) {
    saleJpaRepository.save(saleMapper.toEntity(sale));
    sale.receipt().ifPresent(receipt ->
        electronicReceiptJpaRepository.save(saleMapper.toReceiptEntity(receipt)));
  }

  @Override
  public Optional<Sale> findById(SaleId id) {
    return saleJpaRepository.findById(id.value())
        .map(entity -> saleMapper.toDomain(
            entity, electronicReceiptJpaRepository.findBySaleId(entity.getId()).orElse(null)));
  }

  @Override
  public boolean existsByQuotationId(QuotationId quotationId) {
    return saleJpaRepository.existsByQuotationId(quotationId.value());
  }

  @Override
  public long countIssuedReceipts() {
    return electronicReceiptJpaRepository.count();
  }
}
