package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.domain.model.queries.GetLoyaltyAccountByPatientQuery;
import com.optiflow.platform.notification.domain.model.aggregates.LoyaltyAccount;
import org.springframework.stereotype.Service;
import com.optiflow.platform.notification.application.queryservices.GetLoyaltyAccountByPatientQueryService;

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
