package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.domain.model.commands.RecordPaymentCommand;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentStatus;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.interfaces.rest.resources.RecordPaymentRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromRecordPaymentRequestAssembler {

  public RecordPaymentCommand toCommand(UUID saleId, RecordPaymentRequest request) {
    return new RecordPaymentCommand(
        SaleId.of(saleId),
        PaymentMethod.from(request.method()),
        Money.of(request.amount()),
        request.transactionReference(),
        PaymentStatus.APPROVED,
        null);
  }
}
