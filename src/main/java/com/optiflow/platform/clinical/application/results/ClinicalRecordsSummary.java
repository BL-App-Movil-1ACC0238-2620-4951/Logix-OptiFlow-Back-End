package com.optiflow.platform.clinical.application.results;

/**
 * Result of {@code GetClinicalRecordsSummary}.
 *
 * @param patientsWithRecords patients that have at least one clinical record, in any status
 */
public record ClinicalRecordsSummary(long patientsWithRecords) {
}
