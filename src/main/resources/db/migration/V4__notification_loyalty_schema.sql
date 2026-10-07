CREATE TABLE loyalty_accounts (
  patient_id UUID PRIMARY KEY,
  balance INT NOT NULL,
  last_birthday_discount_year INT,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT loyalty_accounts_patient_fk FOREIGN KEY (patient_id) REFERENCES patients (id),
  CONSTRAINT loyalty_accounts_balance_chk CHECK (balance >= 0)
);

CREATE TABLE notifications (
  id UUID PRIMARY KEY,
  patient_id UUID NOT NULL,
  type VARCHAR(40) NOT NULL,
  title VARCHAR(120) NOT NULL,
  body VARCHAR(500) NOT NULL,
  appointment_id UUID,
  work_order_id UUID,
  status VARCHAR(10) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  read_at TIMESTAMP WITH TIME ZONE,
  CONSTRAINT notifications_patient_fk FOREIGN KEY (patient_id) REFERENCES patients (id),
  CONSTRAINT notifications_type_chk CHECK (type IN (
    'APPOINTMENT_REMINDER',
    'LENS_ORDER_PROGRESS',
    'BIRTHDAY_DISCOUNT',
    'DELIVERY_DELAY'
  )),
  CONSTRAINT notifications_status_chk CHECK (status IN ('UNREAD', 'READ'))
);

CREATE UNIQUE INDEX notifications_patient_appointment_uq
  ON notifications (patient_id, appointment_id)
  WHERE appointment_id IS NOT NULL;
