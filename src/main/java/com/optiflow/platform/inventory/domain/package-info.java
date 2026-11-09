/**
 * Domain layer of the Store Management & Inventory bounded context (report section 2.6.4.1).
 *
 * <p>Expected content: FrameModel, Inventory and Supplier models; events such as
 * NewFrameModelAdded, StockWasReplenished and LowStockAlertGenerated.
 *
 * <p>Subpackages: model/{aggregates,entities,valueobjects,commands,queries,events}, exceptions, repositories, services.
 * Must not depend on Spring, JPA or any other framework.
 */
package com.optiflow.platform.inventory.domain;
