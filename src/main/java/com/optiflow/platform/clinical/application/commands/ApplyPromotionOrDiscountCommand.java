package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.Discount;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;

public record ApplyPromotionOrDiscountCommand(QuotationId quotationId, Discount discount) {
}
