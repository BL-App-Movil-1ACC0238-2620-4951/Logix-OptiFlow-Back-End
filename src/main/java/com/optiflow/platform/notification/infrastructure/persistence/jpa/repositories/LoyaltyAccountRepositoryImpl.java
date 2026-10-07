package com.optiflow.platform.notification.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.notification.domain.entities.LoyaltyAccount;
import com.optiflow.platform.notification.domain.repositories.LoyaltyAccountRepository;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.notification.infrastructure.persistence.jpa.mappers.LoyaltyAccountMapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class LoyaltyAccountRepositoryImpl implements LoyaltyAccountRepository {

  private final LoyaltyAccountJpaRepository loyaltyAccountJpaRepository;
  private final LoyaltyAccountMapper loyaltyAccountMapper;

  public LoyaltyAccountRepositoryImpl(
      LoyaltyAccountJpaRepository loyaltyAccountJpaRepository,
      LoyaltyAccountMapper loyaltyAccountMapper) {
    this.loyaltyAccountJpaRepository = loyaltyAccountJpaRepository;
    this.loyaltyAccountMapper = loyaltyAccountMapper;
  }

  @Override
  public void save(LoyaltyAccount loyaltyAccount) {
    loyaltyAccountJpaRepository.save(loyaltyAccountMapper.toEntity(loyaltyAccount));
  }

  @Override
  public Optional<LoyaltyAccount> findByPatientId(PatientId patientId) {
    return loyaltyAccountJpaRepository.findById(patientId.value())
        .map(loyaltyAccountMapper::toDomain);
  }
}
