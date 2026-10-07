CREATE TABLE patients (
  id UUID PRIMARY KEY,
  full_name VARCHAR(80) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  phone VARCHAR(20) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE optical_stores (
  id UUID PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  address VARCHAR(200) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  rating NUMERIC(3, 2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT optical_stores_rating_chk CHECK (rating >= 0 AND rating <= 5),
  CONSTRAINT optical_stores_status_chk CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE time_slots (
  id UUID PRIMARY KEY,
  optical_store_id UUID NOT NULL,
  start_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
  end_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT time_slots_store_fk FOREIGN KEY (optical_store_id) REFERENCES optical_stores (id),
  CONSTRAINT time_slots_range_chk CHECK (end_date_time > start_date_time),
  CONSTRAINT time_slots_status_chk CHECK (status IN ('AVAILABLE', 'RESERVED', 'BLOCKED'))
);

CREATE TABLE appointments (
  id UUID PRIMARY KEY,
  patient_id UUID NOT NULL,
  optical_store_id UUID NOT NULL,
  time_slot_id UUID NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT appointments_patient_fk FOREIGN KEY (patient_id) REFERENCES patients (id),
  CONSTRAINT appointments_store_fk FOREIGN KEY (optical_store_id) REFERENCES optical_stores (id),
  CONSTRAINT appointments_slot_fk FOREIGN KEY (time_slot_id) REFERENCES time_slots (id),
  CONSTRAINT appointments_status_chk CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'))
);

CREATE TABLE favorite_stores (
  patient_id UUID NOT NULL,
  optical_store_id UUID NOT NULL,
  saved_at TIMESTAMP WITH TIME ZONE NOT NULL,
  PRIMARY KEY (patient_id, optical_store_id),
  CONSTRAINT favorite_stores_patient_fk FOREIGN KEY (patient_id) REFERENCES patients (id),
  CONSTRAINT favorite_stores_store_fk FOREIGN KEY (optical_store_id) REFERENCES optical_stores (id)
);

CREATE TABLE store_ratings (
  patient_id UUID NOT NULL,
  optical_store_id UUID NOT NULL,
  score NUMERIC(3, 2) NOT NULL,
  comment VARCHAR(280),
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  PRIMARY KEY (patient_id, optical_store_id),
  CONSTRAINT store_ratings_patient_fk FOREIGN KEY (patient_id) REFERENCES patients (id),
  CONSTRAINT store_ratings_store_fk FOREIGN KEY (optical_store_id) REFERENCES optical_stores (id),
  CONSTRAINT store_ratings_score_chk CHECK (score >= 1 AND score <= 5)
);

CREATE INDEX time_slots_store_status_idx ON time_slots (optical_store_id, status);
CREATE INDEX appointments_patient_idx ON appointments (patient_id);
CREATE INDEX appointments_time_slot_idx ON appointments (time_slot_id);
