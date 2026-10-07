package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.application.queries.GetLoyaltyAccountByPatientQuery;
import com.optiflow.platform.notification.domain.entities.LoyaltyAccount;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyApplicationService {

  private final GetLoyaltyAccountByPatientQueryService getLoyaltyAccountByPatientQueryService;

  public LoyaltyApplicationService(
      GetLoyaltyAccountByPatientQueryService getLoyaltyAccountByPatientQueryService) {
    this.getLoyaltyAccountByPatientQueryService = getLoyaltyAccountByPatientQueryService;
  }

  public LoyaltyAccount getByPatient(GetLoyaltyAccountByPatientQuery query) {
    return getLoyaltyAccountByPatientQueryService.handle(query);
  }
}
