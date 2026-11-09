package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commandhandlers.CloseSaleHandler;
import com.optiflow.platform.clinical.application.commandhandlers.CreatePaymentPreferenceHandler;
import com.optiflow.platform.clinical.application.commandhandlers.RecordPaymentHandler;
import com.optiflow.platform.clinical.application.commandhandlers.RegisterSaleHandler;
import com.optiflow.platform.clinical.application.outboundservices.PaymentGatewayService;
import com.optiflow.platform.clinical.application.outboundservices.PaymentNotification;
import com.optiflow.platform.clinical.application.outboundservices.PaymentPreference;
import com.optiflow.platform.clinical.application.queryservices.SaleQueryService;
import com.optiflow.platform.clinical.application.results.ClosedSaleDetails;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.commands.CloseSaleCommand;
import com.optiflow.platform.clinical.domain.model.commands.CreatePaymentPreferenceCommand;
import com.optiflow.platform.clinical.domain.model.commands.RecordPaymentCommand;
import com.optiflow.platform.clinical.domain.model.commands.RegisterSaleCommand;
import com.optiflow.platform.clinical.domain.model.queries.GetClosedSaleQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetSaleByIdQuery;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SaleApplicationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleApplicationService.class);

  private final RegisterSaleHandler registerSaleHandler;
  private final CreatePaymentPreferenceHandler createPaymentPreferenceHandler;
  private final RecordPaymentHandler recordPaymentHandler;
  private final CloseSaleHandler closeSaleHandler;
  private final SaleQueryService saleQueryService;
  private final PaymentGatewayService paymentGatewayService;

  public SaleApplicationService(
      RegisterSaleHandler registerSaleHandler,
      CreatePaymentPreferenceHandler createPaymentPreferenceHandler,
      RecordPaymentHandler recordPaymentHandler,
      CloseSaleHandler closeSaleHandler,
      SaleQueryService saleQueryService,
      PaymentGatewayService paymentGatewayService) {
    this.registerSaleHandler = registerSaleHandler;
    this.createPaymentPreferenceHandler = createPaymentPreferenceHandler;
    this.recordPaymentHandler = recordPaymentHandler;
    this.closeSaleHandler = closeSaleHandler;
    this.saleQueryService = saleQueryService;
    this.paymentGatewayService = paymentGatewayService;
  }

  public Sale register(RegisterSaleCommand command) {
    return registerSaleHandler.handle(command);
  }

  public PaymentPreference createPaymentPreference(CreatePaymentPreferenceCommand command) {
    return createPaymentPreferenceHandler.handle(command);
  }

  public Sale recordPayment(RecordPaymentCommand command) {
    return recordPaymentHandler.handle(command);
  }

  /**
   * Records the payment notified by the gateway. A notification that cannot be applied (unknown
   * sale, different amount, sale already paid) is only logged, so the gateway does not retry it.
   */
  public void recordNotifiedPayment(PaymentNotification notification) {
    paymentGatewayService.translate(notification).ifPresent(payment -> {
      try {
        recordPaymentHandler.handle(new RecordPaymentCommand(payment.saleId(), payment.method(),
            payment.amount(), payment.transactionReference(), payment.status(),
            payment.statusDetail()));
      } catch (DomainException exception) {
        LOGGER.warn("Payment {} of sale {} was not recorded: {}",
            payment.transactionReference(), payment.saleId(), exception.getMessage());
      }
    });
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
