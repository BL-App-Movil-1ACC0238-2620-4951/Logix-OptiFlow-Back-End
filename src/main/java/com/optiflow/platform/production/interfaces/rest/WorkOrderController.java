package com.optiflow.platform.production.interfaces.rest;

import com.optiflow.platform.production.application.services.WorkOrderApplicationService;
import com.optiflow.platform.production.domain.model.commands.MarkOrderAsDeliveredCommand;
import com.optiflow.platform.production.domain.model.queries.GetWorkOrderByIdQuery;
import com.optiflow.platform.production.domain.model.queries.GetWorkOrdersByPatientIdQuery;
import com.optiflow.platform.production.domain.model.queries.GetWorkOrdersQuery;
import com.optiflow.platform.production.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.model.valueobjects.PatientId;
import com.optiflow.platform.production.domain.model.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
import com.optiflow.platform.production.interfaces.rest.assemblers.FromAssignWorkOrderToTechnicianRequestAssembler;
import com.optiflow.platform.production.interfaces.rest.assemblers.FromCompleteLensesRequestAssembler;
import com.optiflow.platform.production.interfaces.rest.assemblers.FromGenerateWorkOrderRequestAssembler;
import com.optiflow.platform.production.interfaces.rest.assemblers.FromNotifyDeliveryDelayRequestAssembler;
import com.optiflow.platform.production.interfaces.rest.assemblers.FromSendWorkOrderToLaboratoryRequestAssembler;
import com.optiflow.platform.production.interfaces.rest.assemblers.FromUpdateWorkOrderStatusRequestAssembler;
import com.optiflow.platform.production.interfaces.rest.assemblers.ProductionTrackingResponseAssembler;
import com.optiflow.platform.production.interfaces.rest.resources.AssignWorkOrderToTechnicianRequest;
import com.optiflow.platform.production.interfaces.rest.resources.CompleteLensesRequest;
import com.optiflow.platform.production.interfaces.rest.resources.GenerateWorkOrderRequest;
import com.optiflow.platform.production.interfaces.rest.resources.NotifyDeliveryDelayRequest;
import com.optiflow.platform.production.interfaces.rest.resources.SendWorkOrderToLaboratoryRequest;
import com.optiflow.platform.production.interfaces.rest.resources.UpdateWorkOrderStatusRequest;
import com.optiflow.platform.production.interfaces.rest.resources.WorkOrderResponse;
import com.optiflow.platform.production.interfaces.rest.resources.WorkOrderStatusResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.infrastructure.security.PatientDataAccessGuard;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.WORK_ORDERS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class WorkOrderController {

  private final WorkOrderApplicationService workOrderApplicationService;
  private final FromGenerateWorkOrderRequestAssembler generateAssembler;
  private final FromAssignWorkOrderToTechnicianRequestAssembler assignAssembler;
  private final FromSendWorkOrderToLaboratoryRequestAssembler laboratoryAssembler;
  private final FromUpdateWorkOrderStatusRequestAssembler statusAssembler;
  private final FromCompleteLensesRequestAssembler lensesAssembler;
  private final FromNotifyDeliveryDelayRequestAssembler delayAssembler;
  private final ProductionTrackingResponseAssembler responseAssembler;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public WorkOrderController(
      WorkOrderApplicationService workOrderApplicationService,
      FromGenerateWorkOrderRequestAssembler generateAssembler,
      FromAssignWorkOrderToTechnicianRequestAssembler assignAssembler,
      FromSendWorkOrderToLaboratoryRequestAssembler laboratoryAssembler,
      FromUpdateWorkOrderStatusRequestAssembler statusAssembler,
      FromCompleteLensesRequestAssembler lensesAssembler,
      FromNotifyDeliveryDelayRequestAssembler delayAssembler,
      ProductionTrackingResponseAssembler responseAssembler,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.workOrderApplicationService = workOrderApplicationService;
    this.generateAssembler = generateAssembler;
    this.assignAssembler = assignAssembler;
    this.laboratoryAssembler = laboratoryAssembler;
    this.statusAssembler = statusAssembler;
    this.lensesAssembler = lensesAssembler;
    this.delayAssembler = delayAssembler;
    this.responseAssembler = responseAssembler;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  /** Kanban board: every filter is optional. */
  @Operation(summary = "Search work orders",
      description = "Kanban board of the workshop: filters by status, technician or optical store, "
          + "all optional.")
  @ApiResponses(@ApiResponse(responseCode = "200",
      description = "Work orders ordered by creation date."))
  @GetMapping("/work-orders")
  public List<WorkOrderResponse> search(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) UUID technicianId,
      @RequestParam(required = false) UUID opticalStoreId) {
    GetWorkOrdersQuery query = new GetWorkOrdersQuery(
        status == null || status.isBlank() ? null : WorkOrderStatus.from(status),
        technicianId == null ? null : TechnicianId.of(technicianId),
        opticalStoreId == null ? null : OpticalStoreId.of(opticalStoreId));
    return workOrderApplicationService.search(query).stream()
        .map(responseAssembler::toWorkOrderResponse)
        .toList();
  }

  @Operation(summary = "Generate a work order",
      description = "Generates the work order of a closed sale by hand. Closing a sale already "
          + "generates it through SaleWasClosed.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Work order generated, PENDING."),
      @ApiResponse(responseCode = "404", description = "The sale was not found."),
      @ApiResponse(responseCode = "409",
          description = "The sale is not closed or it already has a work order.")
  })
  @PostMapping("/work-orders")
  public ResponseEntity<WorkOrderResponse> generate(@Valid @RequestBody GenerateWorkOrderRequest request) {
    WorkOrderResponse response = responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.generate(generateAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/work-orders/{id}", response, response.id());
  }

  @Operation(summary = "Get a work order",
      description = "Returns the order with its lenses, technician, laboratory, delay and status "
          + "history.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Work order found."),
      @ApiResponse(responseCode = "404", description = "The work order was not found.")
  })
  @GetMapping("/work-orders/{id}")
  public WorkOrderResponse get(@PathVariable UUID id) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.getById(new GetWorkOrderByIdQuery(WorkOrderId.of(id))));
  }

  @Operation(summary = "List the work orders of a patient",
      description = "Lets the patient follow their orders. A patient can only read their own "
          + "orders.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Work orders of the patient, the newest first."),
      @ApiResponse(responseCode = "403",
          description = "A patient asked for another patient's orders.")
  })
  @GetMapping("/patients/{patientId}/work-orders")
  public List<WorkOrderResponse> findByPatient(
      @PathVariable UUID patientId, @AuthenticationPrincipal AuthenticatedUser user) {
    patientDataAccessGuard.ensureSelfPatientAccess(user, patientId);
    return workOrderApplicationService
        .findByPatient(new GetWorkOrdersByPatientIdQuery(PatientId.of(patientId)))
        .stream()
        .map(responseAssembler::toWorkOrderResponse)
        .toList();
  }

  @Operation(summary = "Assign a technician",
      description = "Assigns the order to a technician of the workshop.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Technician assigned."),
      @ApiResponse(responseCode = "404",
          description = "The work order or the technician was not found."),
      @ApiResponse(responseCode = "409", description = "The order is already finished.")
  })
  @PatchMapping("/work-orders/{id}/technician")
  public WorkOrderResponse assignToTechnician(
      @PathVariable UUID id, @Valid @RequestBody AssignWorkOrderToTechnicianRequest request) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.assignToTechnician(assignAssembler.toCommand(id, request)));
  }

  @Operation(summary = "Send to a laboratory",
      description = "Sends the order to an external laboratory; a pending order moves to "
          + "IN_WORKSHOP.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Order sent to the laboratory."),
      @ApiResponse(responseCode = "404",
          description = "The work order or the laboratory was not found."),
      @ApiResponse(responseCode = "409", description = "The order is already finished.")
  })
  @PatchMapping("/work-orders/{id}/laboratory")
  public WorkOrderResponse sendToLaboratory(
      @PathVariable UUID id, @Valid @RequestBody SendWorkOrderToLaboratoryRequest request) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.sendToLaboratory(laboratoryAssembler.toCommand(id, request)));
  }

  @Operation(summary = "Update the status of a work order",
      description = "Moves the order to the next Kanban stage (IN_WORKSHOP, QUALITY_CONTROL, "
          + "READY_FOR_DELIVERY); stages cannot be skipped. With DELIVERED, registers the "
          + "delivery of an order ready for delivery.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Status updated."),
      @ApiResponse(responseCode = "400", description = "The status is invalid."),
      @ApiResponse(responseCode = "404", description = "The work order was not found."),
      @ApiResponse(responseCode = "409",
          description = "The stage cannot be skipped, the lenses are not completed or the "
              + "order is already delivered.")
  })
  @PatchMapping("/work-orders/{id}/status")
  public WorkOrderStatusResponse updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateWorkOrderStatusRequest request) {
    if (WorkOrderStatus.from(request.status()) == WorkOrderStatus.DELIVERED) {
      return responseAssembler.toStatusResponse(workOrderApplicationService.markAsDelivered(
          new MarkOrderAsDeliveredCommand(WorkOrderId.of(id))));
    }
    return responseAssembler.toStatusResponse(
        workOrderApplicationService.updateStatus(statusAssembler.toCommand(id, request)));
  }

  @Operation(summary = "Complete lenses",
      description = "Marks the given lenses, or every lens when no ids are sent, as manufactured.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Lenses completed."),
      @ApiResponse(responseCode = "400", description = "A lens does not belong to the order."),
      @ApiResponse(responseCode = "404", description = "The work order was not found."),
      @ApiResponse(responseCode = "409", description = "The order is not in the workshop.")
  })
  @PatchMapping("/work-orders/{id}/lenses")
  public WorkOrderResponse completeLenses(
      @PathVariable UUID id,
      @Valid @RequestBody(required = false) CompleteLensesRequest request) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.completeLenses(lensesAssembler.toCommand(id, request)));
  }

  @Operation(summary = "Notify a delivery delay",
      description = "Registers the reason of the delay and the new estimated delivery date, and "
          + "notifies the patient.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Delay registered."),
      @ApiResponse(responseCode = "400",
          description = "The reason is missing or the new date is not later than the current one."),
      @ApiResponse(responseCode = "404", description = "The work order was not found."),
      @ApiResponse(responseCode = "409", description = "The order is already delivered.")
  })
  @PostMapping("/work-orders/{id}/delays")
  public ResponseEntity<WorkOrderResponse> notifyDeliveryDelay(
      @PathVariable UUID id, @Valid @RequestBody NotifyDeliveryDelayRequest request) {
    WorkOrderResponse response = responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.notifyDeliveryDelay(delayAssembler.toCommand(id, request)));
    return CreatedResourceResponses.at("/work-orders/{id}", response, id);
  }
}
