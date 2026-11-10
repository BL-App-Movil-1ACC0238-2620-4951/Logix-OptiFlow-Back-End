package com.optiflow.platform.shared.infrastructure.seed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.TimeSlotEntity;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories.TimeSlotJpaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DataSeederTest {

  private static final ZoneId LIMA = ZoneId.of("America/Lima");
  private static final UUID MIRAFLORES_ID =
      UUID.fromString("11111111-1111-1111-1111-111111111111");

  @Autowired
  private DataSeeder dataSeeder;

  @Autowired
  private TimeSlotJpaRepository timeSlotRepository;

  @Autowired
  private Clock clock;

  @Test
  void doesNotPublishAgainADayWhoseSlotsAreAllReserved() {
    List<TimeSlotEntity> slots = slots();
    assertFalse(slots.isEmpty());
    LocalDate day = dayOf(slots.get(0));
    List<TimeSlotEntity> daySlots = slots.stream().filter(slot -> dayOf(slot).equals(day)).toList();
    daySlots.forEach(slot -> slot.setStatus("RESERVED"));
    timeSlotRepository.saveAllAndFlush(daySlots);

    dataSeeder.run(new DefaultApplicationArguments());

    assertEquals(slots.size(), slots().size());
  }

  private List<TimeSlotEntity> slots() {
    return timeSlotRepository.findByOpticalStoreIdAndStartDateTimeAfter(
        MIRAFLORES_ID, clock.instant());
  }

  private static LocalDate dayOf(TimeSlotEntity slot) {
    return slot.getStartDateTime().atZone(LIMA).toLocalDate();
  }
}
