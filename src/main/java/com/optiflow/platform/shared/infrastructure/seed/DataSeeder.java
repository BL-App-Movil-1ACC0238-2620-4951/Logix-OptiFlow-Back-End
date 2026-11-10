package com.optiflow.platform.shared.infrastructure.seed;

import com.optiflow.platform.inventory.application.services.FrameModelApplicationService;
import com.optiflow.platform.inventory.application.services.InventoryApplicationService;
import com.optiflow.platform.inventory.application.services.SupplierApplicationService;
import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.commands.AddNewFrameModelCommand;
import com.optiflow.platform.inventory.domain.model.commands.RegisterSupplierCommand;
import com.optiflow.platform.inventory.domain.model.commands.ReplenishStockCommand;
import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.domain.model.valueobjects.Price;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import com.optiflow.platform.inventory.domain.repositories.InventoryRepository;
import com.optiflow.platform.inventory.domain.repositories.SupplierRepository;
import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.application.services.PatientApplicationService;
import com.optiflow.platform.searchbooking.application.services.StaffMemberApplicationService;
import com.optiflow.platform.searchbooking.domain.model.commands.PublishAvailableTimeSlotsCommand.SlotDraft;
import com.optiflow.platform.searchbooking.domain.model.commands.PublishAvailableTimeSlotsCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.RegisterStaffMemberCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.GeoLocation;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreName;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreRating;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreStatus;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.UserRole;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories.TimeSlotJpaRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Initial data of OptiFlow: optical stores of Lima with their location, staff of every role, a
 * demo patient, the frame catalog with the stock of each store, suppliers and the time slots of
 * the next days. It runs on every start on the database of the active profile (PostgreSQL on
 * Render, H2 locally) and only inserts what is missing, so it never duplicates nor overwrites.
 */
@Component
public class DataSeeder implements ApplicationRunner {

  static final UUID MIRAFLORES_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  static final UUID SAN_ISIDRO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
  static final UUID SURCO_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");
  static final UUID SAN_MIGUEL_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");
  static final UUID CHORRILLOS_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
  static final String PASSWORD = "secret12";

  private static final ZoneId LIMA = ZoneId.of("America/Lima");
  private static final int SLOT_DAYS = 7;
  private static final int FIRST_HOUR = 9;
  private static final int LAST_HOUR = 17;

  private static final List<StoreSeed> STORES = List.of(
      new StoreSeed(MIRAFLORES_ID, "OptiFlow Miraflores", "Av. Larco 123, Miraflores, Lima",
          "014441111", "4.60", "-12.121037", "-77.028240"),
      new StoreSeed(SAN_ISIDRO_ID, "OptiFlow San Isidro",
          "Av. Javier Prado 456, San Isidro, Lima", "014442222", "4.20", "-12.099000",
          "-77.034000"),
      new StoreSeed(SURCO_ID, "OptiFlow Surco", "Av. Primavera 1050, Santiago de Surco, Lima",
          "014443333", "4.40", "-12.111760", "-76.990650"),
      new StoreSeed(SAN_MIGUEL_ID, "OptiFlow San Miguel",
          "Av. La Marina 2000, San Miguel, Lima", "014444444", "4.10", "-12.077370",
          "-77.082950"),
      new StoreSeed(CHORRILLOS_ID, "OptiFlow Chorrillos",
          "Av. Defensores del Morro 1277, Chorrillos, Lima", "014445555", "4.30", "-12.169870",
          "-77.023060"));

  private static final List<StaffSeed> STAFF = List.of(
      new StaffSeed("OptiFlow Admin", "admin@optiflow.test", MIRAFLORES_ID, UserRole.ADMIN),
      new StaffSeed("Lucía Paredes", "optometrist@optiflow.test", MIRAFLORES_ID,
          UserRole.OPTOMETRIST),
      new StaffSeed("Diego Fernández", "advisor@optiflow.test", MIRAFLORES_ID,
          UserRole.SALES_ADVISOR),
      new StaffSeed("Jorge Salas", "technician@optiflow.test", MIRAFLORES_ID,
          UserRole.TECHNICIAN));

  private static final List<FrameSeed> FRAMES = List.of(
      new FrameSeed("RB-3025-GLD", "Ray-Ban Aviator Classic", "Metal", "Aviador", "Dorado",
          "520.00"),
      new FrameSeed("RB-5154-BLK", "Ray-Ban Clubmaster", "Acetato y metal", "Browline",
          "Negro", "480.00"),
      new FrameSeed("OK-8156-BLK", "Oakley Pitchman", "O Matter", "Rectangular", "Negro mate",
          "450.00"),
      new FrameSeed("PR-17WV-TOR", "Prada PR 17WV", "Acetato", "Ovalada", "Carey", "690.00"),
      new FrameSeed("VO-5276-TRP", "Vogue VO5276", "Acetato", "Mariposa", "Transparente",
          "320.00"),
      new FrameSeed("TF-5401-HAV", "Tom Ford FT5401", "Acetato", "Cuadrada", "Havana",
          "750.00"));

  private final OpticalStoreRepository opticalStoreRepository;
  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final TimeSlotJpaRepository timeSlotRepository;
  private final StaffMemberRepository staffMemberRepository;
  private final StaffMemberApplicationService staffMemberApplicationService;
  private final PatientRepository patientRepository;
  private final PatientApplicationService patientApplicationService;
  private final InventoryRepository inventoryRepository;
  private final InventoryApplicationService inventoryApplicationService;
  private final FrameModelRepository frameModelRepository;
  private final FrameModelApplicationService frameModelApplicationService;
  private final SupplierRepository supplierRepository;
  private final SupplierApplicationService supplierApplicationService;
  private final Clock clock;

  public DataSeeder(
      OpticalStoreRepository opticalStoreRepository,
      OpticalStoreApplicationService opticalStoreApplicationService,
      TimeSlotJpaRepository timeSlotRepository,
      StaffMemberRepository staffMemberRepository,
      StaffMemberApplicationService staffMemberApplicationService,
      PatientRepository patientRepository,
      PatientApplicationService patientApplicationService,
      InventoryRepository inventoryRepository,
      InventoryApplicationService inventoryApplicationService,
      FrameModelRepository frameModelRepository,
      FrameModelApplicationService frameModelApplicationService,
      SupplierRepository supplierRepository,
      SupplierApplicationService supplierApplicationService,
      Clock clock) {
    this.opticalStoreRepository = opticalStoreRepository;
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.timeSlotRepository = timeSlotRepository;
    this.staffMemberRepository = staffMemberRepository;
    this.staffMemberApplicationService = staffMemberApplicationService;
    this.patientRepository = patientRepository;
    this.patientApplicationService = patientApplicationService;
    this.inventoryRepository = inventoryRepository;
    this.inventoryApplicationService = inventoryApplicationService;
    this.frameModelRepository = frameModelRepository;
    this.frameModelApplicationService = frameModelApplicationService;
    this.supplierRepository = supplierRepository;
    this.supplierApplicationService = supplierApplicationService;
    this.clock = clock;
  }

  @Override
  public void run(ApplicationArguments args) {
    seedOpticalStores();
    seedTimeSlots();
    seedStaffMembers();
    seedDemoPatient();
    seedFrameModels();
    List<Supplier> suppliers = seedSuppliers();
    seedInventories(suppliers.get(0));
  }

  private void seedOpticalStores() {
    for (StoreSeed store : STORES) {
      OpticalStoreId id = OpticalStoreId.of(store.id());
      if (opticalStoreRepository.findById(id).isEmpty()) {
        opticalStoreRepository.save(OpticalStore.reconstitute(
            id,
            new StoreName(store.name()),
            new StoreAddress(store.address()),
            new PhoneNumber(store.phone()),
            StoreRating.of(new BigDecimal(store.rating())),
            StoreStatus.ACTIVE,
            new GeoLocation(new BigDecimal(store.latitude()), new BigDecimal(store.longitude()))));
      }
    }
  }

  /**
   * Time slots from 9:00 to 17:00, Monday to Saturday, for the days that still have none. A day
   * whose slots are all reserved already has its slots, so it is not published again.
   */
  private void seedTimeSlots() {
    LocalDate today = LocalDate.now(clock.withZone(LIMA));
    for (StoreSeed store : STORES) {
      OpticalStoreId id = OpticalStoreId.of(store.id());
      Set<LocalDate> daysWithSlots = timeSlotRepository
          .findByOpticalStoreIdAndStartDateTimeAfter(store.id(), clock.instant())
          .stream()
          .map(slot -> slot.getStartDateTime().atZone(LIMA).toLocalDate())
          .collect(Collectors.toSet());
      List<SlotDraft> slots = new ArrayList<>();
      for (int day = 1; day <= SLOT_DAYS; day++) {
        LocalDate date = today.plusDays(day);
        if (date.getDayOfWeek() != DayOfWeek.SUNDAY && !daysWithSlots.contains(date)) {
          for (int hour = FIRST_HOUR; hour < LAST_HOUR; hour++) {
            slots.add(slot(date, hour));
          }
        }
      }
      if (!slots.isEmpty()) {
        opticalStoreApplicationService.publishTimeSlots(
            new PublishAvailableTimeSlotsCommand(id, slots));
      }
    }
  }

  private void seedStaffMembers() {
    for (StaffSeed staff : STAFF) {
      EmailAddress email = new EmailAddress(staff.email());
      if (staffMemberRepository.findByEmail(email).isEmpty()) {
        staffMemberApplicationService.register(new RegisterStaffMemberCommand(
            new Name(staff.name()), email, PASSWORD, OpticalStoreId.of(staff.opticalStoreId()),
            staff.role()));
      }
    }
  }

  private void seedDemoPatient() {
    EmailAddress email = new EmailAddress("patient@optiflow.test");
    if (patientRepository.findByEmail(email).isEmpty()) {
      patientApplicationService.register(new RegisterPatientCommand(
          new Name("Valeria Morales"), email, new PhoneNumber("987654321"), PASSWORD,
          Optional.of(LocalDate.of(1998, 3, 14))));
    }
  }

  private void seedFrameModels() {
    for (FrameSeed frame : FRAMES) {
      Sku sku = Sku.of(frame.sku());
      if (frameModelRepository.findBySku(sku).isEmpty()) {
        frameModelApplicationService.add(new AddNewFrameModelCommand(
            sku, frame.name(), frame.material(), frame.shape(), frame.color(),
            Price.of(new BigDecimal(frame.price())), null));
      }
    }
  }

  private List<Supplier> seedSuppliers() {
    if (supplierRepository.findAll().isEmpty()) {
      supplierApplicationService.register(
          new RegisterSupplierCommand("Luxottica Perú", "ventas@luxottica.pe"));
      supplierApplicationService.register(
          new RegisterSupplierCommand("Distribuidora Óptica Lima", "pedidos@dolima.pe"));
    }
    return supplierRepository.findAll();
  }

  /**
   * Every store gets its inventory; an empty one receives the catalog, with the last frame below
   * its minimum stock so the low stock alert can be seen.
   */
  private void seedInventories(Supplier supplier) {
    UUID admin = staffMemberRepository.findByEmail(new EmailAddress("admin@optiflow.test"))
        .map(StaffMember::id)
        .map(id -> id.value())
        .orElse(null);
    for (StoreSeed store : STORES) {
      var opticalStoreId =
          com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId.of(store.id());
      Inventory inventory = inventoryRepository.findByOpticalStoreId(opticalStoreId)
          .orElseGet(() -> {
            Inventory opened = Inventory.open(opticalStoreId);
            inventoryRepository.save(opened);
            return opened;
          });
      if (!inventory.stocks().isEmpty()) {
        continue;
      }
      for (int i = 0; i < FRAMES.size(); i++) {
        boolean low = i == FRAMES.size() - 1;
        inventoryApplicationService.replenish(new ReplenishStockCommand(
            opticalStoreId, Sku.of(FRAMES.get(i).sku()), supplier.id(), low ? 2 : 10, 3,
            admin));
      }
    }
  }

  private SlotDraft slot(LocalDate day, int hour) {
    return new SlotDraft(
        day.atTime(hour, 0).atZone(LIMA).toInstant(),
        day.atTime(hour, 30).atZone(LIMA).toInstant());
  }

  private record StoreSeed(
      UUID id, String name, String address, String phone, String rating, String latitude,
      String longitude) {
  }

  private record StaffSeed(String name, String email, UUID opticalStoreId, UserRole role) {
  }

  private record FrameSeed(
      String sku, String name, String material, String shape, String color, String price) {
  }
}
