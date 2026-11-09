package com.optiflow.platform.inventory.application.commandhandlers;

import com.optiflow.platform.inventory.domain.model.commands.RegisterSupplierCommand;
import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.domain.model.events.SupplierRegistered;
import com.optiflow.platform.inventory.domain.repositories.SupplierRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterSupplierHandler {

  private final SupplierRepository supplierRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RegisterSupplierHandler(
      SupplierRepository supplierRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.supplierRepository = supplierRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Supplier handle(RegisterSupplierCommand command) {
    Supplier supplier = Supplier.register(command.name(), command.contact());
    supplierRepository.save(supplier);
    eventPublisher.publish(new SupplierRegistered(supplier.id(), supplier.name(), clock.instant()));
    return supplier;
  }
}
