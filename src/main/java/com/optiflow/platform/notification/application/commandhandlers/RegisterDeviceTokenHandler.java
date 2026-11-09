package com.optiflow.platform.notification.application.commandhandlers;

import com.optiflow.platform.notification.domain.model.commands.RegisterDeviceTokenCommand;
import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import com.optiflow.platform.notification.domain.model.events.DeviceTokenRegistered;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.DeviceTokenRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterDeviceTokenHandler {

  private final DeviceTokenRepository deviceTokenRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RegisterDeviceTokenHandler(
      DeviceTokenRepository deviceTokenRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.deviceTokenRepository = deviceTokenRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public DeviceToken handle(RegisterDeviceTokenCommand command) {
    PatientId patientId = PatientId.of(command.patientId());
    Instant now = clock.instant();
    DeviceToken deviceToken = deviceTokenRepository.findByToken(command.token())
        .map(existing -> {
          existing.renew(patientId, command.platform(), now);
          return existing;
        })
        .orElseGet(() -> DeviceToken.register(patientId, command.token(), command.platform(), now));
    deviceTokenRepository.save(deviceToken);
    eventPublisher.publish(new DeviceTokenRegistered(
        deviceToken.id(), patientId, deviceToken.platform(), now));
    return deviceToken;
  }
}
