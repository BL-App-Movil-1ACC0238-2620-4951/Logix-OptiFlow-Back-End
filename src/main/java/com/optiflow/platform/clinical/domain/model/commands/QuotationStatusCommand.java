package com.optiflow.platform.clinical.domain.model.commands;

/** Command that changes the status of a quotation. */
public sealed interface QuotationStatusCommand
    permits ApproveQuotationCommand, RejectQuotationCommand {}
