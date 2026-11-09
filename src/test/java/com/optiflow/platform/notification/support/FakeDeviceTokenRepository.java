package com.optiflow.platform.notification.support;

import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.DeviceTokenRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeDeviceTokenRepository implements DeviceTokenRepository {

  private final Map<String, DeviceToken> store = new ConcurrentHashMap<>();

  @Override
  public void save(DeviceToken deviceToken) {
    store.put(deviceToken.token(), deviceToken);
  }

  @Override
  public Optional<DeviceToken> findByToken(String token) {
    return Optional.ofNullable(store.get(token));
  }

  @Override
  public List<DeviceToken> findByPatientId(PatientId patientId) {
    return store.values().stream()
        .filter(deviceToken -> deviceToken.patientId().equals(patientId))
        .toList();
  }

  @Override
  public void deleteByToken(String token) {
    store.remove(token);
  }
}
