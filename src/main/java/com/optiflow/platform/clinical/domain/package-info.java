/**
 * Domain layer of the Clinical & Commercial bounded context (report section 2.6.2.1).
 *
 * <p>Expected content: ClinicalRecord, Quotation and Sale aggregates; events such as
 * PatientExamined, OpticalPrescriptionGenerated and SaleWasClosed.
 *
 * <p>Subpackages: model/{aggregates,entities,valueobjects,commands,queries,events}, exceptions, repositories, services.
 * Must not depend on Spring, JPA or any other framework.
 */
package com.optiflow.platform.clinical.domain;
