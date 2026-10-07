/**
 * Domain layer of the Store Management & Inventory bounded context (report section 2.6.4.1).
 *
 * <p>Expected content: FrameModel, Inventory and Supplier models; events such as
 * NewFrameModelAdded, StockWasReplenished and LowStockAlertGenerated.
 *
 * <p>Subpackages: entities, events, exceptions, repositories, services, valueobjects.
 * Must not depend on Spring, JPA or any other framework.
 */
package com.optiflow.platform.inventory.domain;
