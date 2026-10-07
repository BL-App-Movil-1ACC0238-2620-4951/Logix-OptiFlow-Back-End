package com.optiflow.platform.production.application.services;

import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import java.util.List;

/**
 * Production view of a sale owned by Clinical & Commercial: only what manufacturing needs.
 *
 * @param opticalStoreId store where the patient was attended; {@code null} when unknown
 * @param lensSpecifications one entry per lens to manufacture, taken from the prescription
 */
public record ClosedSale(
    SaleId saleId,
    PatientId patientId,
    OpticalStoreId opticalStoreId,
    boolean closed,
    List<String> lensSpecifications) {
}
