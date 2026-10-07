package com.optiflow.platform.notification.domain.entities;

import com.optiflow.platform.notification.domain.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.valueobjects.NotificationStatus;
import com.optiflow.platform.notification.domain.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.util.Optional;

public class Notification {

  private final NotificationId id;
  private final PatientId patientId;
  private final NotificationType type;
  private final NotificationMessage message;
  private final AppointmentId appointmentId;
  private final WorkOrderId workOrderId;
  private NotificationStatus status;
  private final Instant createdAt;
  private Instant readAt;

  private Notification(
      NotificationId id,
      PatientId patientId,
      NotificationType type,
      NotificationMessage message,
      AppointmentId appointmentId,
      WorkOrderId workOrderId,
      NotificationStatus status,
      Instant createdAt,
      Instant readAt) {
    this.id = id;
    this.patientId = patientId;
    this.type = type;
    this.message = message;
    this.appointmentId = appointmentId;
    this.workOrderId = workOrderId;
    this.status = status;
    this.createdAt = createdAt;
    this.readAt = readAt;
  }

  public static Notification scheduleAppointmentReminder(
      NotificationId id,
      PatientId patientId,
      AppointmentId appointmentId,
      NotificationMessage message,
      Instant now) {
    return new Notification(
        id,
        patientId,
        NotificationType.APPOINTMENT_REMINDER,
        message,
        appointmentId,
        null,
        NotificationStatus.UNREAD,
        now,
        null);
  }

  public static Notification notifyLensOrderProgress(
      NotificationId id,
      PatientId patientId,
      WorkOrderId workOrderId,
      NotificationMessage message,
      Instant now) {
    return new Notification(
        id,
        patientId,
        NotificationType.LENS_ORDER_PROGRESS,
        message,
        null,
        workOrderId,
        NotificationStatus.UNREAD,
        now,
        null);
  }

  public static Notification notifyDeliveryDelay(
      NotificationId id,
      PatientId patientId,
      WorkOrderId workOrderId,
      NotificationMessage message,
      Instant now) {
    return new Notification(
        id,
        patientId,
        NotificationType.DELIVERY_DELAY,
        message,
        null,
        workOrderId,
        NotificationStatus.UNREAD,
        now,
        null);
  }

  public static Notification sendBirthdayDiscount(
      NotificationId id,
      PatientId patientId,
      NotificationMessage message,
      Instant now) {
    return new Notification(
        id,
        patientId,
        NotificationType.BIRTHDAY_DISCOUNT,
        message,
        null,
        null,
        NotificationStatus.UNREAD,
        now,
        null);
  }

  public static Notification reconstitute(
      NotificationId id,
      PatientId patientId,
      NotificationType type,
      NotificationMessage message,
      AppointmentId appointmentId,
      WorkOrderId workOrderId,
      NotificationStatus status,
      Instant createdAt,
      Instant readAt) {
    return new Notification(
        id, patientId, type, message, appointmentId, workOrderId, status, createdAt, readAt);
  }

  public void markAsRead(Instant now) {
    if (status == NotificationStatus.READ) {
      throw new DomainException("This notification has already been read.", 409);
    }
    status = NotificationStatus.READ;
    readAt = now;
  }

  public NotificationId id() {
    return id;
  }

  public PatientId patientId() {
    return patientId;
  }

  public NotificationType type() {
    return type;
  }

  public NotificationMessage message() {
    return message;
  }

  public Optional<AppointmentId> appointmentId() {
    return Optional.ofNullable(appointmentId);
  }

  public Optional<WorkOrderId> workOrderId() {
    return Optional.ofNullable(workOrderId);
  }

  public NotificationStatus status() {
    return status;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Optional<Instant> readAt() {
    return Optional.ofNullable(readAt);
  }
}
