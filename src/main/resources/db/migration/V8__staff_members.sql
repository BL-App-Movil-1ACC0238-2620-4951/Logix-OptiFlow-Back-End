ALTER TABLE patients ADD COLUMN birth_date DATE NULL;

CREATE TABLE staff_members (
  id UUID PRIMARY KEY,
  optical_store_id UUID NOT NULL,
  full_name VARCHAR(80) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(30) NOT NULL,
  active BOOLEAN NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT staff_members_store_fk FOREIGN KEY (optical_store_id) REFERENCES optical_stores (id),
  CONSTRAINT staff_members_role_chk CHECK (
    role IN ('OPTOMETRIST', 'SALES_ADVISOR', 'TECHNICIAN', 'ADMIN')
  )
);

CREATE INDEX staff_members_store_idx ON staff_members (optical_store_id);
