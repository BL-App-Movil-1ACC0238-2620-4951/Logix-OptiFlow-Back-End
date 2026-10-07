package com.optiflow.platform.production.application.commands;

import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import java.time.LocalDate;

public record NotifyDeliveryDelayCommand(
    WorkOrderId workOrderId,
    String reason,
    LocalDate newEstimatedDeliveryDate) {
}
