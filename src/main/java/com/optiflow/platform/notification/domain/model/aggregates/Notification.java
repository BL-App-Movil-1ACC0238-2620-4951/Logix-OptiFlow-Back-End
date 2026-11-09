package com.optiflow.platform.notification.domain.model.aggregates;

import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.model.valueobjects.PushStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.SurveyResponse;
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
  private SurveyResponse surveyResponse;

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

  /** Reminder of a booked appointment, sent at {@code scheduledAt} (24 hours before it). */
  public static Notification scheduleAppointmentReminder(
      NotificationId id,
      PatientId patientId,
      AppointmentId appointmentId,
      NotificationMessage message,
      Instant scheduledAt,
      Instant now) {
    return scheduled(NotificationType.APPOINTMENT_REMINDER, id, patientId, appointmentId, message,
        scheduledAt, now);
  }

  /** Reminder of the next visual check-up, sent at {@code scheduledAt} (12 months later). */
  public static Notification scheduleAnnualCheckupReminder(
      NotificationId id,
      PatientId patientId,
      AppointmentId appointmentId,
      NotificationMessage message,
      Instant scheduledAt,
      Instant now) {
    return scheduled(NotificationType.ANNUAL_CHECKUP_REMINDER, id, patientId, appointmentId,
        message, scheduledAt, now);
  }

  private static Notification scheduled(
      NotificationType type,
      NotificationId id,
      PatientId patientId,
      AppointmentId appointmentId,
      NotificationMessage message,
      Instant scheduledAt,
      Instant now) {
    if (scheduledAt == null) {
      throw new DomainException("The reminder date is required.", 400);
    }
    return new Notification(id, patientId, type, message, appointmentId, null,
        NotificationStatus.UNREAD, PushStatus.PENDING, scheduledAt, now, null);
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

  /** Satisfaction survey of a delivered order; the patient answers it once. */
  public static Notification sendSatisfactionSurvey(
      NotificationId id,
      PatientId patientId,
      WorkOrderId workOrderId,
      NotificationMessage message,
      Instant now) {
    return new Notification(id, patientId, NotificationType.SATISFACTION_SURVEY, message, null,
        workOrderId, NotificationStatus.UNREAD, PushStatus.PENDING, null, now, null);
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
      Instant readAt,
      SurveyResponse surveyResponse) {
    Notification notification = new Notification(id, patientId, type, message, appointmentId,
        workOrderId, status, pushStatus, scheduledAt, createdAt, readAt);
    notification.surveyResponse = surveyResponse;
    return notification;
  }

  public void markAsRead(Instant now) {
    if (status == NotificationStatus.READ) {
      throw new DomainException("This notification has already been read.", 409);
    }
    status = NotificationStatus.READ;
    readAt = now;
  }

  /** Saves the answer of the patient; answering also marks the survey as read. */
  public void answerSurvey(int rating, String comment, Instant now) {
    if (type != NotificationType.SATISFACTION_SURVEY) {
      throw new DomainException("This notification is not a satisfaction survey.", 409);
    }
    if (surveyResponse != null) {
      throw new DomainException("The satisfaction survey was already answered.", 409);
    }
    surveyResponse = new SurveyResponse(rating, comment, now);
    if (status == NotificationStatus.UNREAD) {
      status = NotificationStatus.READ;
      readAt = now;
    }
  }

  /** A notification without a scheduled date is due right away. */
  public boolean isDue(Instant now) {
    return scheduledAt == null || !scheduledAt.isAfter(now);
  }

  public void markPushSent() {
    pushStatus = PushStatus.SENT;
  }

  /** The notification stays visible in the app even when the push could not be delivered. */
  public void markPushFailed() {
    pushStatus = PushStatus.FAILED;
  }

  public boolean isReminder() {
    return type == NotificationType.APPOINTMENT_REMINDER
        || type == NotificationType.ANNUAL_CHECKUP_REMINDER;
  }

  /** Date the patient sees: the scheduled date of a reminder or the creation date. */
  public Instant date() {
    return scheduledAt == null ? createdAt : scheduledAt;
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

  public Optional<SurveyResponse> surveyResponse() {
    return Optional.ofNullable(surveyResponse);
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
