package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.Discount;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;

public record ApplyPromotionOrDiscountCommand(QuotationId quotationId, Discount discount) {
}
