package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.services.SaleApplicationService;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromPaymentNotificationRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.PaymentNotificationRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Public webhook where the payment gateway notifies the result of each payment. */
@Tag(name = OpenApiTags.SALES)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class PaymentWebhookController {

  private final SaleApplicationService saleApplicationService;
  private final FromPaymentNotificationRequestAssembler notificationAssembler;

  public PaymentWebhookController(
      SaleApplicationService saleApplicationService,
      FromPaymentNotificationRequestAssembler notificationAssembler) {
    this.saleApplicationService = saleApplicationService;
    this.notificationAssembler = notificationAssembler;
  }

  @Operation(summary = "Receive a payment notification",
      description = "Public route registered in Mercado Pago. It checks the x-signature header, "
          + "reads the notified payment and records it: an approved payment pays the sale and a "
          + "rejected one keeps it pending. Repeated notifications are ignored.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Notification received, so the gateway does not send it again."),
      @ApiResponse(responseCode = "401", description = "The signature of the notification is invalid."),
      @ApiResponse(responseCode = "503",
          description = "The payment could not be read from the gateway; it will retry.")
  })
  @SecurityRequirements
  @PostMapping("/payment-notifications")
  public ResponseEntity<Void> receive(
      @Parameter(description = "Id of the notified payment.", example = "1325046318")
      @RequestParam(name = "data.id", required = false) String dataId,
      @Parameter(description = "Notified topic.", example = "payment")
      @RequestParam(required = false) String type,
      @Parameter(description = "Signature of Mercado Pago: ts=<timestamp>,v1=<hmac>.")
      @RequestHeader(name = "x-signature", required = false) String signature,
      @RequestHeader(name = "x-request-id", required = false) String requestId,
      @RequestBody(required = false) PaymentNotificationRequest request) {
    saleApplicationService.recordNotifiedPayment(
        notificationAssembler.toNotification(dataId, type, signature, requestId, request));
    return ResponseEntity.ok().build();
  }
}
