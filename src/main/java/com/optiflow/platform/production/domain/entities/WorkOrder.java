package com.optiflow.platform.production.domain.entities;

import com.optiflow.platform.production.domain.valueobjects.DeliveryDate;
import com.optiflow.platform.production.domain.valueobjects.DeliveryDelay;
import com.optiflow.platform.production.domain.valueobjects.LensId;
import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import com.optiflow.platform.production.domain.valueobjects.StatusChange;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Aggregate root that controls the manufacturing life cycle of an optical order, from the closed
 * sale to its delivery: PENDING, IN_WORKSHOP, QUALITY_CONTROL, READY_FOR_DELIVERY, DELIVERED.
 */
public class WorkOrder {

  private final WorkOrderId id;
  private final SaleId saleId;
  private final PatientId patientId;
  private final OpticalStoreId opticalStoreId;
  private Technician technician;
  private Laboratory laboratory;
  private final List<Lenses> lenses;
  private WorkOrderStatus status;
  private DeliveryDate estimatedDeliveryDate;
  private DeliveryDelay deliveryDelay;
  private final List<StatusChange> statusHistory;
  private final Instant createdAt;
  private Instant updatedAt;
  private Instant deliveredAt;

  private WorkOrder(
      WorkOrderId id,
      SaleId saleId,
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      Technician technician,
      Laboratory laboratory,
      List<Lenses> lenses,
      WorkOrderStatus status,
      DeliveryDate estimatedDeliveryDate,
      DeliveryDelay deliveryDelay,
      List<StatusChange> statusHistory,
      Instant createdAt,
      Instant updatedAt,
      Instant deliveredAt) {
    this.id = id;
    this.saleId = saleId;
    this.patientId = patientId;
    this.opticalStoreId = opticalStoreId;
    this.technician = technician;
    this.laboratory = laboratory;
    this.lenses = new ArrayList<>(lenses);
    this.status = status;
    this.estimatedDeliveryDate = estimatedDeliveryDate;
    this.deliveryDelay = deliveryDelay;
    this.statusHistory = new ArrayList<>(statusHistory);
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deliveredAt = deliveredAt;
  }

  public static WorkOrder generate(
      SaleId saleId,
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      List<String> lensSpecifications,
      LocalDate today,
      Instant now) {
    if (saleId == null || patientId == null) {
      throw new DomainException("A work order needs its sale and patient.", 400);
    }
    if (lensSpecifications == null || lensSpecifications.isEmpty()) {
      throw new DomainException("A work order needs at least one lens to manufacture.", 400);
    }
    return new WorkOrder(
        WorkOrderId.generate(),
        saleId,
        patientId,
        opticalStoreId,
        null,
        null,
        lensSpecifications.stream().map(Lenses::create).toList(),
        WorkOrderStatus.PENDING,
        DeliveryDate.estimateFrom(today),
        null,
        List.of(new StatusChange(WorkOrderStatus.PENDING, now)),
        now,
        now,
        null);
  }

  public static WorkOrder reconstitute(
      WorkOrderId id,
      SaleId saleId,
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      Technician technician,
      Laboratory laboratory,
      List<Lenses> lenses,
      WorkOrderStatus status,
      DeliveryDate estimatedDeliveryDate,
      DeliveryDelay deliveryDelay,
      List<StatusChange> statusHistory,
      Instant createdAt,
      Instant updatedAt,
      Instant deliveredAt) {
    return new WorkOrder(
        id, saleId, patientId, opticalStoreId, technician, laboratory, lenses, status,
        estimatedDeliveryDate, deliveryDelay, statusHistory, createdAt, updatedAt, deliveredAt);
  }

  public void assignTo(Technician technician, Instant now) {
    if (status == WorkOrderStatus.READY_FOR_DELIVERY || status == WorkOrderStatus.DELIVERED) {
      throw new DomainException("A finished work order can no longer be reassigned.", 409);
    }
    this.technician = technician;
    updatedAt = now;
  }

  /** Sends the order to a laboratory; a pending order starts its workshop stage. */
  public void sendToLaboratory(Laboratory laboratory, Instant now) {
    if (status != WorkOrderStatus.PENDING && status != WorkOrderStatus.IN_WORKSHOP) {
      throw new DomainException(
          "Only a pending or in-workshop work order can be sent to a laboratory.", 409);
    }
    this.laboratory = laboratory;
    if (status == WorkOrderStatus.PENDING) {
      changeStatus(WorkOrderStatus.IN_WORKSHOP, now);
    }
    updatedAt = now;
  }

  /** Moves the order to the next Kanban stage; stages cannot be skipped. */
  public void updateStatus(WorkOrderStatus target, Instant now) {
    if (target == null) {
      throw new DomainException("Work order status is required.", 400);
    }
    if (target == WorkOrderStatus.DELIVERED) {
      throw new DomainException(
          "Use the deliver operation to mark the work order as delivered.", 400);
    }
    if (status == WorkOrderStatus.DELIVERED) {
      throw new DomainException("A delivered work order cannot change its status.", 409);
    }
    WorkOrderStatus expected = status.next().orElseThrow();
    if (target != expected) {
      throw new DomainException(
          "The work order is " + status + " and can only move to " + expected
              + "; stages cannot be skipped.",
          409);
    }
    if (target == WorkOrderStatus.QUALITY_CONTROL && !allLensesCompleted()) {
      throw new DomainException(
          "All lenses must be completed before quality control.", 409);
    }
    changeStatus(target, now);
  }

  /** Marks the given lenses (or every lens when the list is empty) as manufactured. */
  public List<LensId> completeLenses(List<LensId> lensIds, Instant now) {
    if (status != WorkOrderStatus.IN_WORKSHOP) {
      throw new DomainException(
          "Lenses can only be completed while the work order is in the workshop.", 409);
    }
    List<Lenses> toComplete = lensIds == null || lensIds.isEmpty()
        ? lenses
        : lensIds.stream().map(this::findLens).toList();
    toComplete.forEach(Lenses::complete);
    updatedAt = now;
    return toComplete.stream().map(Lenses::id).toList();
  }

  public void notifyDeliveryDelay(
      String reason, LocalDate newEstimatedDeliveryDate, LocalDate today, Instant now) {
    if (status == WorkOrderStatus.DELIVERED) {
      throw new DomainException("A delivered work order cannot be delayed.", 409);
    }
    if (newEstimatedDeliveryDate == null
        || !newEstimatedDeliveryDate.isAfter(estimatedDeliveryDate.date())
        || newEstimatedDeliveryDate.isBefore(today)) {
      throw new DomainException(
          "The new estimated delivery date must be after " + estimatedDeliveryDate.date()
              + " and not in the past.",
          400);
    }
    deliveryDelay = new DeliveryDelay(reason, now, newEstimatedDeliveryDate);
    estimatedDeliveryDate = new DeliveryDate(newEstimatedDeliveryDate);
    updatedAt = now;
  }

  public void markAsDelivered(Instant now) {
    if (status != WorkOrderStatus.READY_FOR_DELIVERY) {
      throw new DomainException(
          "Only a work order that is ready for delivery can be delivered.", 409);
    }
    changeStatus(WorkOrderStatus.DELIVERED, now);
    deliveredAt = now;
  }

  public boolean allLensesCompleted() {
    return lenses.stream().allMatch(Lenses::completed);
  }

  public boolean isDelayed(LocalDate today) {
    return deliveryDelay != null
        || (status != WorkOrderStatus.DELIVERED && estimatedDeliveryDate.isOverdue(today));
  }

  private Lenses findLens(LensId lensId) {
    return lenses.stream()
        .filter(lens -> lens.id().equals(lensId))
        .findFirst()
        .orElseThrow(() -> new DomainException(
            "Lens " + lensId.value() + " does not belong to this work order.", 400));
  }

  private void changeStatus(WorkOrderStatus newStatus, Instant now) {
    status = newStatus;
    statusHistory.add(new StatusChange(newStatus, now));
    updatedAt = now;
  }

  public WorkOrderId id() {
    return id;
  }

  public SaleId saleId() {
    return saleId;
  }

  public PatientId patientId() {
    return patientId;
  }

  public Optional<OpticalStoreId> opticalStoreId() {
    return Optional.ofNullable(opticalStoreId);
  }

  public Optional<Technician> technician() {
    return Optional.ofNullable(technician);
  }

  public Optional<Laboratory> laboratory() {
    return Optional.ofNullable(laboratory);
  }

  public List<Lenses> lenses() {
    return List.copyOf(lenses);
  }

  public WorkOrderStatus status() {
    return status;
  }

  public DeliveryDate estimatedDeliveryDate() {
    return estimatedDeliveryDate;
  }

  public Optional<DeliveryDelay> deliveryDelay() {
    return Optional.ofNullable(deliveryDelay);
  }

  public List<StatusChange> statusHistory() {
    return List.copyOf(statusHistory);
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }

  public Instant deliveredAt() {
    return deliveredAt;
  }
}
