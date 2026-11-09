package com.optiflow.platform.inventory.interfaces.events;

import com.optiflow.platform.inventory.application.eventhandlers.SaleWasClosedEventHandler;
import com.optiflow.platform.inventory.domain.model.commands.UpdateInventoryCommand;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.SoldProduct;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.clinical.domain.model.events.SaleWasClosed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Subscribes to the Clinical &amp; Commercial {@link SaleWasClosed} event and translates its sold
 * products into this context (customer/supplier relationship with an anti-corruption layer).
 *
 * <p>It runs after the sale commits.
 */
@Component("inventorySaleWasClosedConsumer")
public class SaleWasClosedConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleWasClosedConsumer.class);

  private final SaleWasClosedEventHandler saleWasClosedEventHandler;

  public SaleWasClosedConsumer(SaleWasClosedEventHandler saleWasClosedEventHandler) {
    this.saleWasClosedEventHandler = saleWasClosedEventHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onSaleWasClosed(SaleWasClosed event) {
    if (event.opticalStoreId() == null || event.soldProducts().isEmpty()) {
      LOGGER.warn("Sale {} has no store or products to take out of the stock.",
          event.saleId().value());
      return;
    }
    saleWasClosedEventHandler.handle(new UpdateInventoryCommand(
        OpticalStoreId.of(event.opticalStoreId()),
        event.saleId().value(),
        event.soldProducts().stream()
            .map(product -> new SoldProduct(Sku.of(product.productSku()), product.quantity()))
            .toList()));
  }
}
