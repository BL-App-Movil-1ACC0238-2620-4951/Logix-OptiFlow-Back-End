package com.optiflow.platform.notification.domain.repositories;

import com.optiflow.platform.notification.domain.model.aggregates.LoyaltyAccount;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import java.util.Optional;

public interface LoyaltyAccountRepository {

  void save(LoyaltyAccount loyaltyAccount);

  Optional<LoyaltyAccount> findByPatientId(PatientId patientId);
}
