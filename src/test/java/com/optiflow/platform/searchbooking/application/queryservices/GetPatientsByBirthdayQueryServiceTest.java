package com.optiflow.platform.searchbooking.application.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.optiflow.platform.searchbooking.application.services.PatientApplicationService;
import com.optiflow.platform.searchbooking.domain.model.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientsByBirthdayQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GetPatientsByBirthdayQueryServiceTest {

  @Autowired private PatientApplicationService patientApplicationService;

  @Test
  void findsPatientsWhoseBirthdayMatchesTheDayAndMonth() {
    LocalDate birthday = LocalDate.of(1990, 3, 15);
    UUID registered = patientApplicationService.register(new RegisterPatientCommand(
            new Name("Birthday Patient"),
            new EmailAddress("birthday.patient@optiflow.test"),
            new PhoneNumber("999000111"),
            "secret12",
            Optional.of(birthday)))
        .id()
        .value();

    LocalDate sameMonthDayDifferentYear = LocalDate.of(2026, 3, 15);
    var matches = patientApplicationService.findByBirthday(
        new GetPatientsByBirthdayQuery(sameMonthDayDifferentYear));

    assertEquals(1, matches.size());
    assertEquals(registered, matches.getFirst().value());
  }
}
