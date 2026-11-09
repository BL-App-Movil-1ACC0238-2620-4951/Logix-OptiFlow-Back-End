/**
 * Domain layer of the Notification & Loyalty bounded context (report section 2.6.5.1).
 *
 * <p>Expected content: notification and loyalty models; events such as BirthdayDiscountWasSent,
 * InAppNotificationSent and LensOrderProgressNotified.
 *
 * <p>Subpackages: model/{aggregates,entities,valueobjects,commands,queries,events}, exceptions, repositories, services.
 * Must not depend on Spring, JPA or any other framework.
 */
package com.optiflow.platform.notification.domain;
