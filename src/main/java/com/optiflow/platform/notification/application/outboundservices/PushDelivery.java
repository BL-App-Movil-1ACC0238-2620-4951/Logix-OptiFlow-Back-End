package com.optiflow.platform.notification.application.outboundservices;

import java.util.List;

/**
 * Result of sending a notification to the devices of a patient.
 *
 * @param invalidTokens tokens the provider no longer accepts; they are removed
 */
public record PushDelivery(int deliveredDevices, List<String> invalidTokens) {

  public static final PushDelivery NONE = new PushDelivery(0, List.of());

  public boolean delivered() {
    return deliveredDevices > 0;
  }
}
