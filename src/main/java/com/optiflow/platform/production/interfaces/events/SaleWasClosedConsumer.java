package com.optiflow.platform.production.interfaces.events;

import com.optiflow.platform.clinical.domain.model.events.SaleWasClosed;
import com.optiflow.platform.production.application.eventhandlers.SaleWasClosedEventHandler;
import com.optiflow.platform.production.domain.model.valueobjects.SaleId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Subscribes to the Clinical & Commercial {@link SaleWasClosed} event and translates it into the
 * identifiers of this context (customer/supplier relationship with an anti-corruption layer).
 *
 * <p>It runs after the sale commits.
 */
@Component
public class SaleWasClosedConsumer {

  private final SaleWasClosedEventHandler saleWasClosedEventHandler;

  public SaleWasClosedConsumer(SaleWasClosedEventHandler saleWasClosedEventHandler) {
    this.saleWasClosedEventHandler = saleWasClosedEventHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onSaleWasClosed(SaleWasClosed event) {
    saleWasClosedEventHandler.handle(SaleId.of(event.saleId().value()));
  }
}
