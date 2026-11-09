package com.optiflow.platform.production.domain.model.commands;

import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import java.time.LocalDate;

public record NotifyDeliveryDelayCommand(
    WorkOrderId workOrderId,
    String reason,
    LocalDate newEstimatedDeliveryDate) {
}
