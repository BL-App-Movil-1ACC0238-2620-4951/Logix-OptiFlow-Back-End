package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.application.commands.RecordPaymentCommand;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
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
        request.transactionReference());
  }
}
