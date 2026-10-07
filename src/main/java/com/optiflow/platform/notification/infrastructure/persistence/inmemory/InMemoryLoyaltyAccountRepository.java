package com.optiflow.platform.notification.infrastructure.persistence.inmemory;

import com.optiflow.platform.notification.domain.entities.LoyaltyAccount;
import com.optiflow.platform.notification.domain.repositories.LoyaltyAccountRepository;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLoyaltyAccountRepository implements LoyaltyAccountRepository {

  private final Map<PatientId, LoyaltyAccount> store = new ConcurrentHashMap<>();

  @Override
  public void save(LoyaltyAccount loyaltyAccount) {
    store.put(loyaltyAccount.patientId(), loyaltyAccount);
  }

  @Override
  public Optional<LoyaltyAccount> findByPatientId(PatientId patientId) {
    return Optional.ofNullable(store.get(patientId));
  }

  public void clear() {
    store.clear();
  }
}
