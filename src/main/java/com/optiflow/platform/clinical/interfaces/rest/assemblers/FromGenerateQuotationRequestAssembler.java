package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.application.commands.GenerateQuotationCommand;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationItemType;
import com.optiflow.platform.clinical.interfaces.rest.resources.GenerateQuotationRequest;
import org.springframework.stereotype.Component;

@Component
public class FromGenerateQuotationRequestAssembler {

  public GenerateQuotationCommand toCommand(GenerateQuotationRequest request) {
    return new GenerateQuotationCommand(
        ClinicalRecordId.of(request.clinicalRecordId()),
        request.items().stream()
            .map(item -> new GenerateQuotationCommand.Item(
                QuotationItemType.from(item.itemType()),
                item.productSku(),
                item.description(),
                Money.of(item.unitPrice()),
                item.quantity()))
            .toList());
  }
}
