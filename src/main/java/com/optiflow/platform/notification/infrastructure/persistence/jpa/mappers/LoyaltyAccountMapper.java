package com.optiflow.platform.notification.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.notification.domain.entities.LoyaltyAccount;
import com.optiflow.platform.notification.domain.valueobjects.LoyaltyPoints;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.notification.infrastructure.persistence.jpa.entities.LoyaltyAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyAccountMapper {

  public LoyaltyAccount toDomain(LoyaltyAccountEntity entity) {
    return LoyaltyAccount.reconstitute(
        PatientId.of(entity.getPatientId()),
        LoyaltyPoints.of(entity.getBalance()),
        entity.getLastBirthdayDiscountYear(),
        entity.getUpdatedAt());
  }

  public LoyaltyAccountEntity toEntity(LoyaltyAccount account) {
    LoyaltyAccountEntity entity = new LoyaltyAccountEntity();
    entity.setPatientId(account.patientId().value());
    entity.setBalance(account.balance().value());
    entity.setLastBirthdayDiscountYear(
        account.lastBirthdayDiscountYear().orElse(null));
    entity.setUpdatedAt(account.updatedAt());
    return entity;
  }
}
