package com.optiflow.platform.notification.domain.model.commands;

import com.optiflow.platform.notification.domain.model.valueobjects.DevicePlatform;
import java.util.UUID;

public record RegisterDeviceTokenCommand(UUID patientId, String token, DevicePlatform platform) {
}
