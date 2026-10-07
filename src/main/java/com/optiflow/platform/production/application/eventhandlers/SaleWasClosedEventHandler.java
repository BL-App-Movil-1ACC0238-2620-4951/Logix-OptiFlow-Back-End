package com.optiflow.platform.production.application.eventhandlers;

import com.optiflow.platform.clinical.domain.events.SaleWasClosed;
import com.optiflow.platform.production.application.commands.GenerateWorkOrderCommand;
import com.optiflow.platform.production.application.services.GenerateWorkOrderHandler;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Consumes the Clinical & Commercial {@link SaleWasClosed} event and starts manufacturing by
 * running the {@code GenerateWorkOrder} command (customer/supplier relationship with an
 * anti-corruption layer, as described in the context map).
 *
 * <p>It runs after the sale commits, so a failure here never rolls back the closed sale.
 */
@Component
public class SaleWasClosedEventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleWasClosedEventHandler.class);

  private final GenerateWorkOrderHandler generateWorkOrderHandler;

  public SaleWasClosedEventHandler(GenerateWorkOrderHandler generateWorkOrderHandler) {
    this.generateWorkOrderHandler = generateWorkOrderHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onSaleWasClosed(SaleWasClosed event) {
    try {
      generateWorkOrderHandler.handle(
          new GenerateWorkOrderCommand(SaleId.of(event.saleId().value())));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not generate the work order for sale {}.", event.saleId().value(), exception);
    }
  }
}
