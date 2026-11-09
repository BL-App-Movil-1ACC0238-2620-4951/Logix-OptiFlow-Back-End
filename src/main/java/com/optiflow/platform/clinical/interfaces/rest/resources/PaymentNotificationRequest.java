package com.optiflow.platform.clinical.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/** Body of a Mercado Pago notification; only the topic and the payment id are used. */
public record PaymentNotificationRequest(
    @Schema(example = "payment") String type,
    @Schema(example = "payment.updated") String action,
    Data data) {

  public record Data(@Schema(description = "Id of the payment in Mercado Pago.",
      example = "1325046318") String id) {
  }
}
