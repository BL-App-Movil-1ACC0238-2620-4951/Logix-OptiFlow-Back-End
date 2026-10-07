/**
 * Domain layer of the Production & Tracking bounded context (report section 2.6.3.1).
 *
 * <p>Expected content: WorkOrder aggregate; events such as WorkOrderGenerated,
 * WorkOrderStatusUpdated and OrderWasMarkedAsDelivered.
 *
 * <p>Subpackages: entities, events, exceptions, repositories, services, valueobjects.
 * Must not depend on Spring, JPA or any other framework.
 */
package com.optiflow.platform.production.domain;
