package com.optiflow.platform.clinical.domain.model.queries;

import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;

public record GetQuotationsByClinicalRecordIdQuery(ClinicalRecordId clinicalRecordId) {
}
