package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.application.outboundservices.PaymentNotification;
import com.optiflow.platform.clinical.interfaces.rest.resources.PaymentNotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class FromPaymentNotificationRequestAssembler {

  /** The query parameters {@code data.id} and {@code type} win over the body, as they are signed. */
  public PaymentNotification toNotification(
      String dataId,
      String type,
      String signature,
      String requestId,
      PaymentNotificationRequest request) {
    String resourceId = dataId != null ? dataId
        : request != null && request.data() != null ? request.data().id() : null;
    String topic = type != null ? type : request != null ? request.type() : null;
    return new PaymentNotification(resourceId, topic, signature, requestId);
  }
}
