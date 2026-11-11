package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commandhandlers.ApplyPromotionOrDiscountHandler;
import com.optiflow.platform.clinical.application.commandhandlers.ApproveQuotationHandler;
import com.optiflow.platform.clinical.application.commandhandlers.GenerateQuotationHandler;
import com.optiflow.platform.clinical.application.commandhandlers.RejectQuotationHandler;
import com.optiflow.platform.clinical.application.queryservices.QuotationQueryService;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.commands.ApplyPromotionOrDiscountCommand;
import com.optiflow.platform.clinical.domain.model.commands.ApproveQuotationCommand;
import com.optiflow.platform.clinical.domain.model.commands.GenerateQuotationCommand;
import com.optiflow.platform.clinical.domain.model.commands.RejectQuotationCommand;
import com.optiflow.platform.clinical.domain.model.queries.GetQuotationByIdQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetQuotationsByClinicalRecordIdQuery;
import java.util.List;
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

  public List<Quotation> getByClinicalRecord(GetQuotationsByClinicalRecordIdQuery query) {
    return quotationQueryService.handle(query);
  }
}
