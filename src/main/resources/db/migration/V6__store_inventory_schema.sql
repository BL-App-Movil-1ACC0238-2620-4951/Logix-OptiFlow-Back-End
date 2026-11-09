CREATE TABLE frame_models (
  id UUID PRIMARY KEY,
  sku VARCHAR(100) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  material VARCHAR(100),
  shape VARCHAR(100),
  color VARCHAR(100),
  price_amount NUMERIC(10, 2) NOT NULL,
  price_currency VARCHAR(10) NOT NULL,
  try_on_model_url VARCHAR(255),
  CONSTRAINT frame_models_price_chk CHECK (price_amount > 0)
);

-- One inventory per optical store.
CREATE TABLE inventories (
  id UUID PRIMARY KEY,
  optical_store_id UUID NOT NULL UNIQUE,
  CONSTRAINT inventories_store_fk FOREIGN KEY (optical_store_id) REFERENCES optical_stores (id)
);

CREATE TABLE suppliers (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  contact VARCHAR(255)
);

CREATE TABLE inventory_stocks (
  inventory_id UUID NOT NULL,
  frame_model_id UUID NOT NULL,
  quantity INTEGER NOT NULL,
  minimum_stock INTEGER NOT NULL,
  PRIMARY KEY (inventory_id, frame_model_id),
  CONSTRAINT inventory_stocks_inventory_fk FOREIGN KEY (inventory_id) REFERENCES inventories (id),
  CONSTRAINT inventory_stocks_frame_fk FOREIGN KEY (frame_model_id) REFERENCES frame_models (id),
  CONSTRAINT inventory_stocks_quantity_chk CHECK (quantity >= 0),
  CONSTRAINT inventory_stocks_minimum_chk CHECK (minimum_stock >= 0)
);

-- performed_by is empty for the exits of a sale, which come from the SaleWasClosed event.
CREATE TABLE stock_movements (
  id UUID PRIMARY KEY,
  inventory_id UUID NOT NULL,
  frame_model_id UUID NOT NULL,
  type VARCHAR(10) NOT NULL,
  reason VARCHAR(20) NOT NULL,
  quantity INTEGER NOT NULL,
  sale_id UUID,
  supplier_id UUID,
  performed_by UUID,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT stock_movements_inventory_fk FOREIGN KEY (inventory_id) REFERENCES inventories (id),
  CONSTRAINT stock_movements_frame_fk FOREIGN KEY (frame_model_id) REFERENCES frame_models (id),
  CONSTRAINT stock_movements_supplier_fk FOREIGN KEY (supplier_id) REFERENCES suppliers (id),
  CONSTRAINT stock_movements_type_chk CHECK (type IN ('IN', 'OUT')),
  CONSTRAINT stock_movements_reason_chk CHECK (reason IN ('REPLENISHMENT', 'SALE', 'ADJUSTMENT')),
  CONSTRAINT stock_movements_quantity_chk CHECK (quantity > 0)
);

CREATE INDEX stock_movements_inventory_date_idx ON stock_movements (inventory_id, created_at);
