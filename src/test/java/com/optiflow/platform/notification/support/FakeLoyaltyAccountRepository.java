package com.optiflow.platform.notification.support;

import com.optiflow.platform.notification.domain.entities.LoyaltyAccount;
import com.optiflow.platform.notification.domain.repositories.LoyaltyAccountRepository;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeLoyaltyAccountRepository implements LoyaltyAccountRepository {

  private final Map<PatientId, LoyaltyAccount> store = new ConcurrentHashMap<>();

  @Override
  public void save(LoyaltyAccount loyaltyAccount) {
    store.put(loyaltyAccount.patientId(), loyaltyAccount);
  }

  @Override
  public Optional<LoyaltyAccount> findByPatientId(PatientId patientId) {
    return Optional.ofNullable(store.get(patientId));
  }
}
