package com.optiflow.platform.clinical.infrastructure.outboundservices;

import com.optiflow.platform.clinical.application.services.PaymentGatewayService;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.PaymentMethod;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Anti-corruption layer for the external payment gateway (POS, Yape, Plin).
 *
 * <p>No real provider is integrated yet: every charge is approved and, when the cashier does not
 * send a reference (for example, cash payments), a local one is generated.
 */
@Component
public class PaymentGatewayAdapter implements PaymentGatewayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGatewayAdapter.class);

  @Override
  public String authorize(PaymentMethod method, Money amount, String transactionReference) {
    String reference = transactionReference == null || transactionReference.isBlank()
        ? method.name() + "-" + UUID.randomUUID().toString()
            .substring(0, 8).toUpperCase(Locale.ROOT)
        : transactionReference.trim();
    LOGGER.info("Payment of {} via {} authorized with reference {}.", amount, method, reference);
    return reference;
  }
}
