package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commandhandlers.GrantBirthdayDiscountHandler;
import com.optiflow.platform.notification.application.outboundservices.ExternalPatientDirectoryService;
import com.optiflow.platform.notification.domain.model.commands.GrantBirthdayDiscountCommand;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Every day at 08:00 in Lima grants the birthday discount to the patients born that day. */
@Component
public class BirthdayDiscountScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(BirthdayDiscountScheduler.class);
  private static final String LIMA = "America/Lima";

  private final ExternalPatientDirectoryService patientDirectoryService;
  private final GrantBirthdayDiscountHandler grantBirthdayDiscountHandler;
  private final Clock clock;

  public BirthdayDiscountScheduler(
      ExternalPatientDirectoryService patientDirectoryService,
      GrantBirthdayDiscountHandler grantBirthdayDiscountHandler,
      Clock clock) {
    this.patientDirectoryService = patientDirectoryService;
    this.grantBirthdayDiscountHandler = grantBirthdayDiscountHandler;
    this.clock = clock;
  }

  @Scheduled(cron = "0 0 8 * * *", zone = LIMA)
  public void grantBirthdayDiscounts() {
    LocalDate today = LocalDate.now(clock.withZone(ZoneId.of(LIMA)));
    for (PatientId patientId : patientDirectoryService.findPatientsWithBirthday(today)) {
      try {
        grantBirthdayDiscountHandler.handle(
            new GrantBirthdayDiscountCommand(patientId.value(), today.getYear()));
      } catch (DomainException exception) {
        // 409: the discount of this year was already granted, for example after a restart.
        if (exception.status() != 409) {
          LOGGER.error(
              "Could not grant the birthday discount to {}.", patientId.value(), exception);
        }
      }
    }
  }
}
