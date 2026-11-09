package com.optiflow.platform.notification.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import com.optiflow.platform.notification.domain.model.valueobjects.DevicePlatform;
import com.optiflow.platform.notification.domain.model.valueobjects.DeviceTokenId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.infrastructure.persistence.jpa.entities.DeviceTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class DeviceTokenMapper {

  public DeviceToken toDomain(DeviceTokenEntity entity) {
    return DeviceToken.reconstitute(
        DeviceTokenId.of(entity.getId()),
        PatientId.of(entity.getPatientId()),
        entity.getToken(),
        DevicePlatform.valueOf(entity.getPlatform()),
        entity.getUpdatedAt());
  }

  public DeviceTokenEntity toEntity(DeviceToken deviceToken) {
    DeviceTokenEntity entity = new DeviceTokenEntity();
    entity.setId(deviceToken.id().value());
    entity.setPatientId(deviceToken.patientId().value());
    entity.setToken(deviceToken.token());
    entity.setPlatform(deviceToken.platform().name());
    entity.setUpdatedAt(deviceToken.updatedAt());
    return entity;
  }
}
