package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.PaymentMethod;

/** Outbound port to the external payment gateway (POS, Yape, Plin). */
public interface PaymentGatewayService {

  /** Authorizes the charge and returns the gateway transaction reference. */
  String authorize(PaymentMethod method, Money amount, String transactionReference);
}
