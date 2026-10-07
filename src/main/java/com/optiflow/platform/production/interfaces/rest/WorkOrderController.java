package com.optiflow.platform.production.interfaces.rest;

import com.optiflow.platform.production.application.commands.MarkOrderAsDeliveredCommand;
import com.optiflow.platform.production.application.queries.GetWorkOrderByIdQuery;
import com.optiflow.platform.production.application.queries.GetWorkOrdersByPatientIdQuery;
import com.optiflow.platform.production.application.queries.GetWorkOrdersQuery;
import com.optiflow.platform.production.application.services.WorkOrderApplicationService;
import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
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
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.WORK_ORDERS)
@RestController
public class WorkOrderController {

  private final WorkOrderApplicationService workOrderApplicationService;
  private final FromGenerateWorkOrderRequestAssembler generateAssembler;
  private final FromAssignWorkOrderToTechnicianRequestAssembler assignAssembler;
  private final FromSendWorkOrderToLaboratoryRequestAssembler laboratoryAssembler;
  private final FromUpdateWorkOrderStatusRequestAssembler statusAssembler;
  private final FromCompleteLensesRequestAssembler lensesAssembler;
  private final FromNotifyDeliveryDelayRequestAssembler delayAssembler;
  private final ProductionTrackingResponseAssembler responseAssembler;

  public WorkOrderController(
      WorkOrderApplicationService workOrderApplicationService,
      FromGenerateWorkOrderRequestAssembler generateAssembler,
      FromAssignWorkOrderToTechnicianRequestAssembler assignAssembler,
      FromSendWorkOrderToLaboratoryRequestAssembler laboratoryAssembler,
      FromUpdateWorkOrderStatusRequestAssembler statusAssembler,
      FromCompleteLensesRequestAssembler lensesAssembler,
      FromNotifyDeliveryDelayRequestAssembler delayAssembler,
      ProductionTrackingResponseAssembler responseAssembler) {
    this.workOrderApplicationService = workOrderApplicationService;
    this.generateAssembler = generateAssembler;
    this.assignAssembler = assignAssembler;
    this.laboratoryAssembler = laboratoryAssembler;
    this.statusAssembler = statusAssembler;
    this.lensesAssembler = lensesAssembler;
    this.delayAssembler = delayAssembler;
    this.responseAssembler = responseAssembler;
  }

  /** Kanban board: every filter is optional. */
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

  @PostMapping("/work-orders")
  @ResponseStatus(HttpStatus.CREATED)
  public WorkOrderResponse generate(@Valid @RequestBody GenerateWorkOrderRequest request) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.generate(generateAssembler.toCommand(request)));
  }

  @GetMapping("/work-orders/{id}")
  public WorkOrderResponse get(@PathVariable UUID id) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.getById(new GetWorkOrderByIdQuery(WorkOrderId.of(id))));
  }

  @GetMapping("/patients/{patientId}/work-orders")
  public List<WorkOrderResponse> findByPatient(@PathVariable UUID patientId) {
    return workOrderApplicationService
        .findByPatient(new GetWorkOrdersByPatientIdQuery(PatientId.of(patientId)))
        .stream()
        .map(responseAssembler::toWorkOrderResponse)
        .toList();
  }

  @PatchMapping("/work-orders/{id}/technician")
  public WorkOrderResponse assignToTechnician(
      @PathVariable UUID id, @Valid @RequestBody AssignWorkOrderToTechnicianRequest request) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.assignToTechnician(assignAssembler.toCommand(id, request)));
  }

  @PatchMapping("/work-orders/{id}/laboratory")
  public WorkOrderResponse sendToLaboratory(
      @PathVariable UUID id, @Valid @RequestBody SendWorkOrderToLaboratoryRequest request) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.sendToLaboratory(laboratoryAssembler.toCommand(id, request)));
  }

  @PatchMapping("/work-orders/{id}/status")
  public WorkOrderStatusResponse updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateWorkOrderStatusRequest request) {
    return responseAssembler.toStatusResponse(
        workOrderApplicationService.updateStatus(statusAssembler.toCommand(id, request)));
  }

  @PatchMapping("/work-orders/{id}/lenses/complete")
  public WorkOrderResponse completeLenses(
      @PathVariable UUID id,
      @Valid @RequestBody(required = false) CompleteLensesRequest request) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.completeLenses(lensesAssembler.toCommand(id, request)));
  }

  @PostMapping("/work-orders/{id}/delays")
  @ResponseStatus(HttpStatus.CREATED)
  public WorkOrderResponse notifyDeliveryDelay(
      @PathVariable UUID id, @Valid @RequestBody NotifyDeliveryDelayRequest request) {
    return responseAssembler.toWorkOrderResponse(
        workOrderApplicationService.notifyDeliveryDelay(delayAssembler.toCommand(id, request)));
  }

  @PatchMapping("/work-orders/{id}/deliver")
  public WorkOrderStatusResponse markAsDelivered(@PathVariable UUID id) {
    return responseAssembler.toStatusResponse(workOrderApplicationService.markAsDelivered(
        new MarkOrderAsDeliveredCommand(WorkOrderId.of(id))));
  }
}
