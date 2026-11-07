package com.optiflow.platform.notification.application.queryservices;

import com.optiflow.platform.notification.domain.exceptions.LoyaltyAccountNotFoundException;
import com.optiflow.platform.notification.domain.model.aggregates.LoyaltyAccount;
import com.optiflow.platform.notification.domain.model.queries.GetLoyaltyAccountByPatientQuery;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.LoyaltyAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetLoyaltyAccountByPatientQueryService {

  private final LoyaltyAccountRepository loyaltyAccountRepository;

  public GetLoyaltyAccountByPatientQueryService(LoyaltyAccountRepository loyaltyAccountRepository) {
    this.loyaltyAccountRepository = loyaltyAccountRepository;
  }

  @Transactional(readOnly = true)
  public LoyaltyAccount handle(GetLoyaltyAccountByPatientQuery query) {
    return loyaltyAccountRepository.findByPatientId(PatientId.of(query.patientId()))
        .orElseThrow(LoyaltyAccountNotFoundException::new);
  }
}
