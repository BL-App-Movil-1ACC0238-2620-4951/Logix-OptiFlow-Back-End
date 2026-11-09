package com.optiflow.platform.notification.domain.model.aggregates;

import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.model.valueobjects.PushStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
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
  private PushStatus pushStatus;
  private final Instant scheduledAt;
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
      PushStatus pushStatus,
      Instant scheduledAt,
      Instant createdAt,
      Instant readAt) {
    this.id = id;
    this.patientId = patientId;
    this.type = type;
    this.message = message;
    this.appointmentId = appointmentId;
    this.workOrderId = workOrderId;
    this.status = status;
    this.pushStatus = pushStatus;
    this.scheduledAt = scheduledAt;
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
        PushStatus.PENDING,
        null,
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
        PushStatus.PENDING,
        null,
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
        PushStatus.PENDING,
        null,
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
        PushStatus.PENDING,
        null,
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
      PushStatus pushStatus,
      Instant scheduledAt,
      Instant createdAt,
      Instant readAt) {
    return new Notification(id, patientId, type, message, appointmentId, workOrderId, status,
        pushStatus, scheduledAt, createdAt, readAt);
  }

  public void markAsRead(Instant now) {
    if (status == NotificationStatus.READ) {
      throw new DomainException("This notification has already been read.", 409);
    }
    status = NotificationStatus.READ;
    readAt = now;
  }

  public void markPushSent() {
    pushStatus = PushStatus.SENT;
  }

  /** The notification stays visible in the app even when the push could not be delivered. */
  public void markPushFailed() {
    pushStatus = PushStatus.FAILED;
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

  public PushStatus pushStatus() {
    return pushStatus;
  }

  /** Date when a reminder is sent; empty for notifications sent right away. */
  public Optional<Instant> scheduledAt() {
    return Optional.ofNullable(scheduledAt);
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Optional<Instant> readAt() {
    return Optional.ofNullable(readAt);
  }
}
