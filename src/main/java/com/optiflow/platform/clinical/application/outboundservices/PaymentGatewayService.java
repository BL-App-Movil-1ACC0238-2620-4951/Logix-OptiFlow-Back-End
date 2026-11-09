package com.optiflow.platform.clinical.application.outboundservices;

import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import java.util.Optional;

/** Outbound port to the external payment gateway (Mercado Pago Checkout Pro). */
public interface PaymentGatewayService {

  /** Creates the preference that the patient pays with card or Yape on the gateway page. */
  PaymentPreference createPreference(SaleId saleId, Money amount, String title);

  /**
   * Checks the signature of a notification and returns the notified payment once it is approved
   * or rejected. It is empty for other topics and for payments still in process.
   */
  Optional<NotifiedPayment> translate(PaymentNotification notification);

  /** Authorizes the charge and returns the gateway transaction reference. */
  String authorize(PaymentMethod method, Money amount, String transactionReference);
}
