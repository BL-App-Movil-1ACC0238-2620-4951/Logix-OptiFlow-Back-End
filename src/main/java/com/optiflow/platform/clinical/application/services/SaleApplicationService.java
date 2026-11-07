package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commandhandlers.CloseSaleHandler;
import com.optiflow.platform.clinical.application.commandhandlers.RecordPaymentHandler;
import com.optiflow.platform.clinical.application.commandhandlers.RegisterSaleHandler;
import com.optiflow.platform.clinical.application.queryservices.SaleQueryService;
import com.optiflow.platform.clinical.application.results.ClosedSaleDetails;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.commands.CloseSaleCommand;
import com.optiflow.platform.clinical.domain.model.commands.RecordPaymentCommand;
import com.optiflow.platform.clinical.domain.model.commands.RegisterSaleCommand;
import com.optiflow.platform.clinical.domain.model.queries.GetClosedSaleQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetSaleByIdQuery;
import org.springframework.stereotype.Service;

@Service
public class SaleApplicationService {

  private final RegisterSaleHandler registerSaleHandler;
  private final RecordPaymentHandler recordPaymentHandler;
  private final CloseSaleHandler closeSaleHandler;
  private final SaleQueryService saleQueryService;

  public SaleApplicationService(
      RegisterSaleHandler registerSaleHandler,
      RecordPaymentHandler recordPaymentHandler,
      CloseSaleHandler closeSaleHandler,
      SaleQueryService saleQueryService) {
    this.registerSaleHandler = registerSaleHandler;
    this.recordPaymentHandler = recordPaymentHandler;
    this.closeSaleHandler = closeSaleHandler;
    this.saleQueryService = saleQueryService;
  }

  public Sale register(RegisterSaleCommand command) {
    return registerSaleHandler.handle(command);
  }

  public Sale recordPayment(RecordPaymentCommand command) {
    return recordPaymentHandler.handle(command);
  }

  public Sale close(CloseSaleCommand command) {
    return closeSaleHandler.handle(command);
  }

  public Sale getById(GetSaleByIdQuery query) {
    return saleQueryService.handle(query);
  }

  public ClosedSaleDetails getClosedSale(GetClosedSaleQuery query) {
    return saleQueryService.handle(query);
  }
}
