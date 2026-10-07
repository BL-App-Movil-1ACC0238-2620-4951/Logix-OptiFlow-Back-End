CREATE TABLE technicians (
  id UUID PRIMARY KEY,
  name VARCHAR(120) NOT NULL
);

CREATE TABLE laboratories (
  id UUID PRIMARY KEY,
  name VARCHAR(120) NOT NULL
);

-- Technician, laboratory and delivery delay are embedded in the work order, as in the
-- document model of the report: they are snapshots, not foreign keys.
CREATE TABLE work_orders (
  id UUID PRIMARY KEY,
  sale_id UUID NOT NULL UNIQUE,
  patient_id UUID NOT NULL,
  optical_store_id UUID,
  technician_id UUID,
  technician_name VARCHAR(120),
  laboratory_id UUID,
  laboratory_name VARCHAR(120),
  status VARCHAR(30) NOT NULL,
  estimated_delivery_date DATE NOT NULL,
  delay_reason VARCHAR(255),
  delay_reported_at TIMESTAMP WITH TIME ZONE,
  delay_new_estimated_date DATE,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  delivered_at TIMESTAMP WITH TIME ZONE,
  CONSTRAINT work_orders_status_chk CHECK (status IN (
    'PENDING', 'IN_WORKSHOP', 'QUALITY_CONTROL', 'READY_FOR_DELIVERY', 'DELIVERED'))
);

CREATE TABLE work_order_lenses (
  id UUID PRIMARY KEY,
  work_order_id UUID NOT NULL,
  line_number INTEGER NOT NULL,
  specifications VARCHAR(500) NOT NULL,
  completed BOOLEAN NOT NULL,
  CONSTRAINT work_order_lenses_order_fk FOREIGN KEY (work_order_id) REFERENCES work_orders (id),
  CONSTRAINT work_order_lenses_line_uq UNIQUE (work_order_id, line_number)
);

CREATE TABLE work_order_status_history (
  work_order_id UUID NOT NULL,
  sequence_number INTEGER NOT NULL,
  status VARCHAR(30) NOT NULL,
  changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
  PRIMARY KEY (work_order_id, sequence_number),
  CONSTRAINT work_order_status_history_order_fk
    FOREIGN KEY (work_order_id) REFERENCES work_orders (id)
);

CREATE INDEX work_orders_status_idx ON work_orders (status);
CREATE INDEX work_orders_patient_idx ON work_orders (patient_id);
CREATE INDEX work_orders_technician_idx ON work_orders (technician_id);
