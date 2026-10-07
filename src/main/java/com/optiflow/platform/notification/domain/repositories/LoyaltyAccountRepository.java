package com.optiflow.platform.notification.domain.repositories;

import com.optiflow.platform.notification.domain.entities.LoyaltyAccount;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import java.util.Optional;

public interface LoyaltyAccountRepository {

  void save(LoyaltyAccount loyaltyAccount);

  Optional<LoyaltyAccount> findByPatientId(PatientId patientId);
}
