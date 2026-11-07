package com.optiflow.platform.clinical.domain.model.queries;

import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;

public record GetQuotationByIdQuery(QuotationId quotationId) {
}
