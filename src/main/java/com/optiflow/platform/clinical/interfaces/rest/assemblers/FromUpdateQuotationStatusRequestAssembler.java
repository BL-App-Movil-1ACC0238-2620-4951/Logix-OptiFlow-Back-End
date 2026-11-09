package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.domain.model.commands.ApproveQuotationCommand;
import com.optiflow.platform.clinical.domain.model.commands.QuotationStatusCommand;
import com.optiflow.platform.clinical.domain.model.commands.RejectQuotationCommand;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationStatus;
import com.optiflow.platform.clinical.interfaces.rest.resources.UpdateQuotationStatusRequest;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromUpdateQuotationStatusRequestAssembler {

  public QuotationStatusCommand toCommand(UUID quotationId, UpdateQuotationStatusRequest request) {
    QuotationId id = QuotationId.of(quotationId);
    QuotationStatus status = parseQuotationStatus(request.status());
    return switch (status) {
      case APPROVED -> new ApproveQuotationCommand(id);
      case REJECTED -> {
        String reason = request.reason();
        if (reason == null || reason.isBlank()) {
          throw new DomainException("A rejection reason is required.", 400);
        }
        yield new RejectQuotationCommand(id, reason);
      }
      case DRAFT -> throw new DomainException("Quotation status cannot be changed to DRAFT.", 400);
    };
  }

  private static QuotationStatus parseQuotationStatus(String status) {
    try {
      return QuotationStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException exception) {
      throw new DomainException("Quotation status must be APPROVED or REJECTED.", 400);
    }
  }
}
