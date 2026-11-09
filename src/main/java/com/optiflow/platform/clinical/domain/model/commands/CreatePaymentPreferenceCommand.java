package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;

/**
 * The patient asks to pay a sale from the app.
 *
 * @param patientId patient who asks to pay; the sale must be theirs
 */
public record CreatePaymentPreferenceCommand(SaleId saleId, PatientId patientId) {
}
