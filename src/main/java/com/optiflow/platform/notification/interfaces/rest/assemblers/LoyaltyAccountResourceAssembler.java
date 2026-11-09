package com.optiflow.platform.notification.interfaces.rest.assemblers;

import com.optiflow.platform.notification.domain.model.aggregates.LoyaltyAccount;
import com.optiflow.platform.notification.interfaces.rest.resources.LoyaltyAccountResponse;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyAccountResourceAssembler {

  public LoyaltyAccountResponse toResponse(LoyaltyAccount account) {
    return new LoyaltyAccountResponse(
        account.patientId().value(),
        account.balance().value(),
        account.lastBirthdayDiscountYear().orElse(null),
        account.updatedAt());
  }
}
