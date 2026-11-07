package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;

public record RejectQuotationCommand(QuotationId quotationId, String reason)
    implements QuotationStatusCommand {
}
