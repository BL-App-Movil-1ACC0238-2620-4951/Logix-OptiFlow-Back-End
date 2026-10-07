package com.optiflow.booking.infrastructure.config;

import com.optiflow.booking.application.command.PublishAvailableTimeSlotsCommand;
import com.optiflow.booking.application.command.PublishAvailableTimeSlotsCommand.SlotDraft;
import com.optiflow.booking.application.service.OpticalStoreApplicationService;
import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.model.StoreStatus;
import com.optiflow.booking.domain.repository.OpticalStoreRepository;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PhoneNumber;
import com.optiflow.booking.domain.vo.StoreAddress;
import com.optiflow.booking.domain.vo.StoreName;
import com.optiflow.booking.domain.vo.StoreRating;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SearchBookingDataSeeder implements ApplicationRunner {

  static final UUID MIRAFLORES_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  static final UUID SAN_ISIDRO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

  private static final ZoneId LIMA = ZoneId.of("America/Lima");

  private final OpticalStoreRepository opticalStoreRepository;
  private final OpticalStoreApplicationService opticalStoreApplicationService;

  public SearchBookingDataSeeder(
      OpticalStoreRepository opticalStoreRepository,
      OpticalStoreApplicationService opticalStoreApplicationService) {
    this.opticalStoreRepository = opticalStoreRepository;
    this.opticalStoreApplicationService = opticalStoreApplicationService;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (!opticalStoreRepository.isEmpty()) {
      return;
    }
    saveStore(
        MIRAFLORES_ID,
        "OptiFlow Miraflores",
        "Av. Larco 123, Miraflores, Lima",
        "014441111",
        "4.60");
    saveStore(
        SAN_ISIDRO_ID,
        "OptiFlow San Isidro",
        "Av. Javier Prado 456, San Isidro, Lima",
        "014442222",
        "4.20");
    LocalDate tomorrow = LocalDate.now(LIMA).plusDays(1);
    publishSlots(MIRAFLORES_ID, tomorrow);
    publishSlots(SAN_ISIDRO_ID, tomorrow);
  }

  private void saveStore(UUID id, String name, String address, String phone, String rating) {
    opticalStoreRepository.save(OpticalStore.reconstitute(
        OpticalStoreId.of(id),
        new StoreName(name),
        new StoreAddress(address),
        new PhoneNumber(phone),
        StoreRating.of(new BigDecimal(rating)),
        StoreStatus.ACTIVE));
  }

  private void publishSlots(UUID storeId, LocalDate day) {
    opticalStoreApplicationService.publishTimeSlots(new PublishAvailableTimeSlotsCommand(
        OpticalStoreId.of(storeId),
        List.of(
            slot(day, 9),
            slot(day, 10),
            slot(day, 11))));
  }

  private SlotDraft slot(LocalDate day, int hour) {
    return new SlotDraft(
        day.atTime(hour, 0).atZone(LIMA).toInstant(),
        day.atTime(hour, 30).atZone(LIMA).toInstant());
  }
}
