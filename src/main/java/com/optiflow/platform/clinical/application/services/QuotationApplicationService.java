package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.ApplyPromotionOrDiscountCommand;
import com.optiflow.platform.clinical.application.commands.ApproveQuotationCommand;
import com.optiflow.platform.clinical.application.commands.GenerateQuotationCommand;
import com.optiflow.platform.clinical.application.commands.RejectQuotationCommand;
import com.optiflow.platform.clinical.application.queries.GetQuotationByIdQuery;
import com.optiflow.platform.clinical.domain.entities.Quotation;
import org.springframework.stereotype.Service;

@Service
public class QuotationApplicationService {

  private final GenerateQuotationHandler generateQuotationHandler;
  private final ApplyPromotionOrDiscountHandler applyPromotionOrDiscountHandler;
  private final ApproveQuotationHandler approveQuotationHandler;
  private final RejectQuotationHandler rejectQuotationHandler;
  private final QuotationQueryService quotationQueryService;

  public QuotationApplicationService(
      GenerateQuotationHandler generateQuotationHandler,
      ApplyPromotionOrDiscountHandler applyPromotionOrDiscountHandler,
      ApproveQuotationHandler approveQuotationHandler,
      RejectQuotationHandler rejectQuotationHandler,
      QuotationQueryService quotationQueryService) {
    this.generateQuotationHandler = generateQuotationHandler;
    this.applyPromotionOrDiscountHandler = applyPromotionOrDiscountHandler;
    this.approveQuotationHandler = approveQuotationHandler;
    this.rejectQuotationHandler = rejectQuotationHandler;
    this.quotationQueryService = quotationQueryService;
  }

  public Quotation generate(GenerateQuotationCommand command) {
    return generateQuotationHandler.handle(command);
  }

  public Quotation applyPromotionOrDiscount(ApplyPromotionOrDiscountCommand command) {
    return applyPromotionOrDiscountHandler.handle(command);
  }

  public Quotation approve(ApproveQuotationCommand command) {
    return approveQuotationHandler.handle(command);
  }

  public Quotation reject(RejectQuotationCommand command) {
    return rejectQuotationHandler.handle(command);
  }

  public Quotation getById(GetQuotationByIdQuery query) {
    return quotationQueryService.handle(query);
  }
}
