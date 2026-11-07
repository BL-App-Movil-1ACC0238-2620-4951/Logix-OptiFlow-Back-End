package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.domain.model.commands.ApplyPromotionOrDiscountCommand;
import com.optiflow.platform.clinical.domain.model.valueobjects.Discount;
import com.optiflow.platform.clinical.domain.model.valueobjects.DiscountType;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.interfaces.rest.resources.ApplyPromotionOrDiscountRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromApplyPromotionOrDiscountRequestAssembler {

  public ApplyPromotionOrDiscountCommand toCommand(
      UUID quotationId, ApplyPromotionOrDiscountRequest request) {
    return new ApplyPromotionOrDiscountCommand(
        QuotationId.of(quotationId),
        new Discount(DiscountType.from(request.type()), request.value(), request.reason()));
  }
}
