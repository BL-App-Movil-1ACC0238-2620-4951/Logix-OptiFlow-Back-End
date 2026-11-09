package com.optiflow.platform.production.application.eventhandlers;

import com.optiflow.platform.production.application.commandhandlers.GenerateWorkOrderHandler;
import com.optiflow.platform.production.domain.model.commands.GenerateWorkOrderCommand;
import com.optiflow.platform.production.domain.model.valueobjects.SaleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Reacts to a closed sale received by the {@code SaleWasClosedConsumer} and starts manufacturing
 * by running the {@code GenerateWorkOrder} command.
 *
 * <p>A failure is logged and never rolls back the closed sale.
 */
@Component
public class SaleWasClosedEventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleWasClosedEventHandler.class);

  private final GenerateWorkOrderHandler generateWorkOrderHandler;

  public SaleWasClosedEventHandler(GenerateWorkOrderHandler generateWorkOrderHandler) {
    this.generateWorkOrderHandler = generateWorkOrderHandler;
  }

  public void handle(SaleId saleId) {
    try {
      generateWorkOrderHandler.handle(new GenerateWorkOrderCommand(saleId));
    } catch (RuntimeException exception) {
      LOGGER.error("Could not generate the work order for sale {}.", saleId.value(), exception);
    }
  }
}
