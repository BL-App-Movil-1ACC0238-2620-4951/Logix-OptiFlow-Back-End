package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;

public record RejectQuotationCommand(QuotationId quotationId, String reason) {
}
