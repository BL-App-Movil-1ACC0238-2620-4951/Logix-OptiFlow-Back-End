package com.optiflow.platform.production.application.services;

import com.optiflow.platform.production.application.commandhandlers.AssignWorkOrderToTechnicianHandler;
import com.optiflow.platform.production.application.commandhandlers.CompleteLensesHandler;
import com.optiflow.platform.production.application.commandhandlers.GenerateWorkOrderHandler;
import com.optiflow.platform.production.application.commandhandlers.MarkOrderAsDeliveredHandler;
import com.optiflow.platform.production.application.commandhandlers.NotifyDeliveryDelayHandler;
import com.optiflow.platform.production.application.commandhandlers.SendWorkOrderToLaboratoryHandler;
import com.optiflow.platform.production.application.commandhandlers.UpdateWorkOrderStatusHandler;
import com.optiflow.platform.production.application.queryservices.ProductionCatalogQueryService;
import com.optiflow.platform.production.application.queryservices.WorkOrderQueryService;
import com.optiflow.platform.production.domain.model.aggregates.WorkOrder;
import com.optiflow.platform.production.domain.model.commands.AssignWorkOrderToTechnicianCommand;
import com.optiflow.platform.production.domain.model.commands.CompleteLensesCommand;
import com.optiflow.platform.production.domain.model.commands.GenerateWorkOrderCommand;
import com.optiflow.platform.production.domain.model.commands.MarkOrderAsDeliveredCommand;
import com.optiflow.platform.production.domain.model.commands.NotifyDeliveryDelayCommand;
import com.optiflow.platform.production.domain.model.commands.SendWorkOrderToLaboratoryCommand;
import com.optiflow.platform.production.domain.model.commands.UpdateWorkOrderStatusCommand;
import com.optiflow.platform.production.domain.model.entities.Laboratory;
import com.optiflow.platform.production.domain.model.entities.Technician;
import com.optiflow.platform.production.domain.model.queries.GetLaboratoriesQuery;
import com.optiflow.platform.production.domain.model.queries.GetTechniciansQuery;
import com.optiflow.platform.production.domain.model.queries.GetWorkOrderByIdQuery;
import com.optiflow.platform.production.domain.model.queries.GetWorkOrdersByPatientIdQuery;
import com.optiflow.platform.production.domain.model.queries.GetWorkOrdersQuery;
import java.util.List;
import org.springframework.stereotype.Service;

/** Facade of the generation, assignment, laboratory, tracking and delivery use cases. */
@Service
public class WorkOrderApplicationService {

  private final GenerateWorkOrderHandler generateWorkOrderHandler;
  private final AssignWorkOrderToTechnicianHandler assignWorkOrderToTechnicianHandler;
  private final SendWorkOrderToLaboratoryHandler sendWorkOrderToLaboratoryHandler;
  private final UpdateWorkOrderStatusHandler updateWorkOrderStatusHandler;
  private final CompleteLensesHandler completeLensesHandler;
  private final NotifyDeliveryDelayHandler notifyDeliveryDelayHandler;
  private final MarkOrderAsDeliveredHandler markOrderAsDeliveredHandler;
  private final WorkOrderQueryService workOrderQueryService;
  private final ProductionCatalogQueryService productionCatalogQueryService;

  public WorkOrderApplicationService(
      GenerateWorkOrderHandler generateWorkOrderHandler,
      AssignWorkOrderToTechnicianHandler assignWorkOrderToTechnicianHandler,
      SendWorkOrderToLaboratoryHandler sendWorkOrderToLaboratoryHandler,
      UpdateWorkOrderStatusHandler updateWorkOrderStatusHandler,
      CompleteLensesHandler completeLensesHandler,
      NotifyDeliveryDelayHandler notifyDeliveryDelayHandler,
      MarkOrderAsDeliveredHandler markOrderAsDeliveredHandler,
      WorkOrderQueryService workOrderQueryService,
      ProductionCatalogQueryService productionCatalogQueryService) {
    this.generateWorkOrderHandler = generateWorkOrderHandler;
    this.assignWorkOrderToTechnicianHandler = assignWorkOrderToTechnicianHandler;
    this.sendWorkOrderToLaboratoryHandler = sendWorkOrderToLaboratoryHandler;
    this.updateWorkOrderStatusHandler = updateWorkOrderStatusHandler;
    this.completeLensesHandler = completeLensesHandler;
    this.notifyDeliveryDelayHandler = notifyDeliveryDelayHandler;
    this.markOrderAsDeliveredHandler = markOrderAsDeliveredHandler;
    this.workOrderQueryService = workOrderQueryService;
    this.productionCatalogQueryService = productionCatalogQueryService;
  }

  public WorkOrder generate(GenerateWorkOrderCommand command) {
    return generateWorkOrderHandler.handle(command);
  }

  public WorkOrder assignToTechnician(AssignWorkOrderToTechnicianCommand command) {
    return assignWorkOrderToTechnicianHandler.handle(command);
  }

  public WorkOrder sendToLaboratory(SendWorkOrderToLaboratoryCommand command) {
    return sendWorkOrderToLaboratoryHandler.handle(command);
  }

  public WorkOrder updateStatus(UpdateWorkOrderStatusCommand command) {
    return updateWorkOrderStatusHandler.handle(command);
  }

  public WorkOrder completeLenses(CompleteLensesCommand command) {
    return completeLensesHandler.handle(command);
  }

  public WorkOrder notifyDeliveryDelay(NotifyDeliveryDelayCommand command) {
    return notifyDeliveryDelayHandler.handle(command);
  }

  public WorkOrder markAsDelivered(MarkOrderAsDeliveredCommand command) {
    return markOrderAsDeliveredHandler.handle(command);
  }

  public WorkOrder getById(GetWorkOrderByIdQuery query) {
    return workOrderQueryService.handle(query);
  }

  public List<WorkOrder> search(GetWorkOrdersQuery query) {
    return workOrderQueryService.handle(query);
  }

  public List<WorkOrder> findByPatient(GetWorkOrdersByPatientIdQuery query) {
    return workOrderQueryService.handle(query);
  }

  public List<Technician> technicians(GetTechniciansQuery query) {
    return productionCatalogQueryService.handle(query);
  }

  public List<Laboratory> laboratories(GetLaboratoriesQuery query) {
    return productionCatalogQueryService.handle(query);
  }
}
