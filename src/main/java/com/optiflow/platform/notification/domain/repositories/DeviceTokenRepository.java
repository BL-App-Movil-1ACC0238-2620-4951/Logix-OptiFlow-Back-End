package com.optiflow.platform.notification.domain.repositories;

import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository {

  void save(DeviceToken deviceToken);

  Optional<DeviceToken> findByToken(String token);

  List<DeviceToken> findByPatientId(PatientId patientId);

  void deleteByToken(String token);
}
