package com.optiflow.platform.clinical.application.outboundservices;

import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;

/** Outbound port to the external payment gateway (Mercado Pago Checkout Pro). */
public interface PaymentGatewayService {

  /** Creates the preference that the patient pays with card or Yape on the gateway page. */
  PaymentPreference createPreference(SaleId saleId, Money amount, String title);

  /** Authorizes the charge and returns the gateway transaction reference. */
  String authorize(PaymentMethod method, Money amount, String transactionReference);
}
