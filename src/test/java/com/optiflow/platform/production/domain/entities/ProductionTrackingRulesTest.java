package com.optiflow.platform.production.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.valueobjects.LensId;
import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import com.optiflow.platform.production.domain.valueobjects.StatusChange;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProductionTrackingRulesTest {

  private static final Instant NOW = Instant.parse("2026-10-07T15:00:00Z");
  private static final LocalDate TODAY = LocalDate.parse("2026-10-07");

  @Test
  void generatesAPendingOrderWithItsEstimatedDeliveryDate() {
    WorkOrder workOrder = newWorkOrder();

    assertEquals(WorkOrderStatus.PENDING, workOrder.status());
    assertEquals(LocalDate.parse("2026-10-14"), workOrder.estimatedDeliveryDate().date());
    assertEquals(2, workOrder.lenses().size());
    assertEquals(List.of(new StatusChange(WorkOrderStatus.PENDING, NOW)), workOrder.statusHistory());
    assertFalse(workOrder.isDelayed(TODAY));
  }

  @Test
  void doesNotAllowSkippingKanbanStages() {
    WorkOrder workOrder = newWorkOrder();

    DomainException exception = assertThrows(DomainException.class,
        () -> workOrder.updateStatus(WorkOrderStatus.QUALITY_CONTROL, NOW));

    assertEquals(409, exception.status());
    assertEquals(WorkOrderStatus.PENDING, workOrder.status());
  }

  @Test
  void requiresCompletedLensesBeforeQualityControl() {
    WorkOrder workOrder = newWorkOrder();
    workOrder.sendToLaboratory(laboratory(), NOW);

    assertEquals(WorkOrderStatus.IN_WORKSHOP, workOrder.status());
    assertThrows(DomainException.class,
        () -> workOrder.updateStatus(WorkOrderStatus.QUALITY_CONTROL, NOW));

    List<LensId> completed = workOrder.completeLenses(List.of(), NOW);
    workOrder.updateStatus(WorkOrderStatus.QUALITY_CONTROL, NOW);

    assertEquals(2, completed.size());
    assertEquals(WorkOrderStatus.QUALITY_CONTROL, workOrder.status());
  }

  @Test
  void deliversOnlyWhenReadyAndKeepsTheWholeHistory() {
    WorkOrder workOrder = newWorkOrder();
    assertThrows(DomainException.class, () -> workOrder.markAsDelivered(NOW));

    workOrder.sendToLaboratory(laboratory(), NOW);
    workOrder.completeLenses(List.of(), NOW);
    workOrder.updateStatus(WorkOrderStatus.QUALITY_CONTROL, NOW);
    workOrder.updateStatus(WorkOrderStatus.READY_FOR_DELIVERY, NOW);
    workOrder.markAsDelivered(NOW);

    assertEquals(WorkOrderStatus.DELIVERED, workOrder.status());
    assertEquals(NOW, workOrder.deliveredAt());
    assertEquals(5, workOrder.statusHistory().size());
    assertThrows(DomainException.class,
        () -> workOrder.updateStatus(WorkOrderStatus.READY_FOR_DELIVERY, NOW));
  }

  @Test
  void aDelayMustPostponeTheEstimatedDeliveryDate() {
    WorkOrder workOrder = newWorkOrder();

    DomainException exception = assertThrows(DomainException.class,
        () -> workOrder.notifyDeliveryDelay("Falta de stock", TODAY.plusDays(3), TODAY, NOW));
    assertEquals(400, exception.status());

    workOrder.notifyDeliveryDelay("Falta de stock de lunas", TODAY.plusDays(10), TODAY, NOW);

    assertEquals(TODAY.plusDays(10), workOrder.estimatedDeliveryDate().date());
    assertTrue(workOrder.isDelayed(TODAY));
  }

  private static WorkOrder newWorkOrder() {
    return WorkOrder.generate(
        SaleId.generate(),
        PatientId.generate(),
        OpticalStoreId.generate(),
        List.of("OD: ESF -1.25 CIL -0.50 EJE 90°", "OS: ESF -1.00 CIL -0.75 EJE 85°"),
        TODAY,
        NOW);
  }

  private static Laboratory laboratory() {
    return new Laboratory(LaboratoryId.generate(), "Laboratorio Central OptiFlow");
  }
}
