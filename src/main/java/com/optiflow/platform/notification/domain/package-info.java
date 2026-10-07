/**
 * Domain layer of the Notification & Loyalty bounded context (report section 2.6.5.1).
 *
 * <p>Expected content: notification and loyalty models; events such as BirthdayDiscountWasSent,
 * InAppNotificationSent and LensOrderProgressNotified.
 *
 * <p>Subpackages: entities, events, exceptions, repositories, services, valueobjects.
 * Must not depend on Spring, JPA or any other framework.
 */
package com.optiflow.platform.notification.domain;
