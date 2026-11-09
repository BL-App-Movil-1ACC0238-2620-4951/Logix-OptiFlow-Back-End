package com.optiflow.platform.notification.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.DeviceTokenRepository;
import com.optiflow.platform.notification.infrastructure.persistence.jpa.mappers.DeviceTokenMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DeviceTokenRepositoryImpl implements DeviceTokenRepository {

  private final DeviceTokenJpaRepository deviceTokenJpaRepository;
  private final DeviceTokenMapper deviceTokenMapper;

  public DeviceTokenRepositoryImpl(
      DeviceTokenJpaRepository deviceTokenJpaRepository, DeviceTokenMapper deviceTokenMapper) {
    this.deviceTokenJpaRepository = deviceTokenJpaRepository;
    this.deviceTokenMapper = deviceTokenMapper;
  }

  @Override
  public void save(DeviceToken deviceToken) {
    deviceTokenJpaRepository.save(deviceTokenMapper.toEntity(deviceToken));
  }

  @Override
  public Optional<DeviceToken> findByToken(String token) {
    return deviceTokenJpaRepository.findByToken(token).map(deviceTokenMapper::toDomain);
  }

  @Override
  public List<DeviceToken> findByPatientId(PatientId patientId) {
    return deviceTokenJpaRepository.findByPatientId(patientId.value()).stream()
        .map(deviceTokenMapper::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public void deleteByToken(String token) {
    deviceTokenJpaRepository.deleteByToken(token);
  }
}
