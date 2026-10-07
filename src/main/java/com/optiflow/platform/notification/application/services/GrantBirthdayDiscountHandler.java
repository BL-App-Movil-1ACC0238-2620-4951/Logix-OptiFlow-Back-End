package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.application.commands.GrantBirthdayDiscountCommand;
import com.optiflow.platform.notification.domain.entities.LoyaltyAccount;
import com.optiflow.platform.notification.domain.entities.Notification;
import com.optiflow.platform.notification.domain.events.BirthdayDiscountWasSent;
import com.optiflow.platform.notification.domain.events.InAppNotificationSent;
import com.optiflow.platform.notification.domain.repositories.LoyaltyAccountRepository;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.notification.domain.valueobjects.LoyaltyPoints;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GrantBirthdayDiscountHandler {

  private final LoyaltyAccountRepository loyaltyAccountRepository;
  private final NotificationRepository notificationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public GrantBirthdayDiscountHandler(
      LoyaltyAccountRepository loyaltyAccountRepository,
      NotificationRepository notificationRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.loyaltyAccountRepository = loyaltyAccountRepository;
    this.notificationRepository = notificationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Notification handle(GrantBirthdayDiscountCommand command) {
    PatientId patientId = PatientId.of(command.patientId());
    Instant now = clock.instant();
    LoyaltyAccount account = loyaltyAccountRepository.findByPatientId(patientId)
        .orElseGet(() -> LoyaltyAccount.open(patientId, now));
    LoyaltyPoints bonus = account.grantBirthdayDiscount(command.calendarYear(), now);
    loyaltyAccountRepository.save(account);

    Notification notification = Notification.sendBirthdayDiscount(
        NotificationId.generate(),
        patientId,
        new NotificationMessage(
            "Happy birthday!",
            "We added " + bonus.value() + " loyalty points to your account."),
        now);
    notificationRepository.save(notification);
    eventPublisher.publish(new BirthdayDiscountWasSent(
        notification.id(), patientId, bonus, command.calendarYear(), now));
    eventPublisher.publish(new InAppNotificationSent(
        notification.id(), patientId, notification.type(), now));
    return notification;
  }
}
