package com.optiflow.platform.notification.interfaces.rest.assemblers;

import com.optiflow.platform.notification.domain.model.commands.RegisterDeviceTokenCommand;
import com.optiflow.platform.notification.domain.model.valueobjects.DevicePlatform;
import com.optiflow.platform.notification.interfaces.rest.resources.RegisterDeviceTokenRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromRegisterDeviceTokenRequestAssembler {

  public RegisterDeviceTokenCommand toCommand(UUID patientId, RegisterDeviceTokenRequest request) {
    return new RegisterDeviceTokenCommand(
        patientId, request.token().trim(), DevicePlatform.valueOf(request.platform()));
  }
}
