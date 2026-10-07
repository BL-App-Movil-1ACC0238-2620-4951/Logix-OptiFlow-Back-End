CREATE TABLE clinical_records (
  id UUID PRIMARY KEY,
  patient_id UUID NOT NULL,
  appointment_id UUID NOT NULL UNIQUE,
  examination_date TIMESTAMP WITH TIME ZONE,
  observations VARCHAR(500),
  status VARCHAR(30) NOT NULL,
  CONSTRAINT clinical_records_status_chk CHECK (status IN ('PENDING', 'OPEN', 'CLOSED'))
);

CREATE TABLE medical_histories (
  clinical_record_id UUID PRIMARY KEY,
  allergies VARCHAR(1000),
  previous_conditions VARCHAR(1000),
  family_ocular_history VARCHAR(1000),
  last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT medical_histories_record_fk
    FOREIGN KEY (clinical_record_id) REFERENCES clinical_records (id)
);

CREATE TABLE optical_prescriptions (
  clinical_record_id UUID PRIMARY KEY,
  sphere_od NUMERIC(5, 2) NOT NULL,
  sphere_os NUMERIC(5, 2) NOT NULL,
  cylinder_od NUMERIC(5, 2) NOT NULL,
  cylinder_os NUMERIC(5, 2) NOT NULL,
  axis_od SMALLINT NOT NULL,
  axis_os SMALLINT NOT NULL,
  addition NUMERIC(5, 2),
  treatment VARCHAR(255),
  recommended_frame_type VARCHAR(255),
  CONSTRAINT optical_prescriptions_record_fk
    FOREIGN KEY (clinical_record_id) REFERENCES clinical_records (id),
  CONSTRAINT optical_prescriptions_axis_chk
    CHECK (axis_od BETWEEN 0 AND 180 AND axis_os BETWEEN 0 AND 180)
);

CREATE TABLE quotations (
  id UUID PRIMARY KEY,
  clinical_record_id UUID NOT NULL,
  discount_type VARCHAR(30),
  discount_value NUMERIC(10, 2),
  discount_reason VARCHAR(255),
  total NUMERIC(10, 2) NOT NULL,
  status VARCHAR(30) NOT NULL,
  rejection_reason VARCHAR(255),
  CONSTRAINT quotations_record_fk
    FOREIGN KEY (clinical_record_id) REFERENCES clinical_records (id),
  CONSTRAINT quotations_status_chk CHECK (status IN ('DRAFT', 'APPROVED', 'REJECTED')),
  CONSTRAINT quotations_discount_type_chk
    CHECK (discount_type IS NULL OR discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
  CONSTRAINT quotations_total_chk CHECK (total >= 0)
);

CREATE TABLE quotation_items (
  id UUID PRIMARY KEY,
  quotation_id UUID NOT NULL,
  line_number INTEGER NOT NULL,
  item_type VARCHAR(30) NOT NULL,
  product_sku VARCHAR(60),
  description VARCHAR(255) NOT NULL,
  unit_price NUMERIC(10, 2) NOT NULL,
  quantity INTEGER NOT NULL,
  CONSTRAINT quotation_items_quotation_fk
    FOREIGN KEY (quotation_id) REFERENCES quotations (id),
  CONSTRAINT quotation_items_line_uq UNIQUE (quotation_id, line_number),
  CONSTRAINT quotation_items_type_chk
    CHECK (item_type IN ('FRAME', 'LENS', 'TREATMENT', 'SERVICE')),
  CONSTRAINT quotation_items_price_chk CHECK (unit_price >= 0),
  CONSTRAINT quotation_items_quantity_chk CHECK (quantity BETWEEN 1 AND 100)
);

CREATE TABLE sales (
  id UUID PRIMARY KEY,
  quotation_id UUID NOT NULL UNIQUE,
  patient_id UUID NOT NULL,
  status VARCHAR(30) NOT NULL,
  closed_at TIMESTAMP WITH TIME ZONE,
  payment_method VARCHAR(30),
  payment_amount NUMERIC(10, 2),
  transaction_reference VARCHAR(255),
  paid_at TIMESTAMP WITH TIME ZONE,
  CONSTRAINT sales_quotation_fk FOREIGN KEY (quotation_id) REFERENCES quotations (id),
  CONSTRAINT sales_status_chk CHECK (status IN ('PENDING_PAYMENT', 'PAID', 'CLOSED')),
  CONSTRAINT sales_payment_method_chk
    CHECK (payment_method IS NULL OR payment_method IN ('CASH', 'CARD', 'YAPE', 'PLIN'))
);

CREATE TABLE electronic_receipts (
  id UUID PRIMARY KEY,
  sale_id UUID NOT NULL UNIQUE,
  receipt_number VARCHAR(100) NOT NULL UNIQUE,
  issue_date TIMESTAMP WITH TIME ZONE NOT NULL,
  tax_amount NUMERIC(10, 2) NOT NULL,
  total_amount NUMERIC(10, 2) NOT NULL,
  CONSTRAINT electronic_receipts_sale_fk FOREIGN KEY (sale_id) REFERENCES sales (id)
);

CREATE INDEX clinical_records_patient_idx ON clinical_records (patient_id);
CREATE INDEX quotations_record_idx ON quotations (clinical_record_id);
