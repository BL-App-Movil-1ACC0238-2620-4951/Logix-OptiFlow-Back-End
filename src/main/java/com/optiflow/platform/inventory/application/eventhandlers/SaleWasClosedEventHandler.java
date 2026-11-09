package com.optiflow.platform.inventory.application.eventhandlers;

import com.optiflow.platform.inventory.application.commandhandlers.UpdateInventoryHandler;
import com.optiflow.platform.inventory.domain.model.commands.UpdateInventoryCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Reacts to a closed sale received by the {@code SaleWasClosedConsumer} and runs
 * {@code UpdateInventory}.
 *
 * <p>A failure is logged and never rolls back the closed sale.
 */
@Component("inventorySaleWasClosedEventHandler")
public class SaleWasClosedEventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleWasClosedEventHandler.class);

  private final UpdateInventoryHandler updateInventoryHandler;

  public SaleWasClosedEventHandler(UpdateInventoryHandler updateInventoryHandler) {
    this.updateInventoryHandler = updateInventoryHandler;
  }

  public void handle(UpdateInventoryCommand command) {
    try {
      updateInventoryHandler.handle(command);
    } catch (RuntimeException exception) {
      LOGGER.error("Could not update the inventory for sale {}.", command.saleId(), exception);
    }
  }
}
