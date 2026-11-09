-- Checkout Pro (Mercado Pago): card and Yape payments come from the gateway and cash from the
-- sales advisor. Plin is no longer accepted; the few sales paid with it become Yape payments,
-- the other mobile wallet.
UPDATE sales SET payment_method = 'YAPE' WHERE payment_method = 'PLIN';

ALTER TABLE sales DROP CONSTRAINT sales_payment_method_chk;

ALTER TABLE sales ADD CONSTRAINT sales_payment_method_chk
  CHECK (payment_method IS NULL OR payment_method IN ('CASH', 'CARD', 'YAPE'));

-- Preference created in the gateway when the patient chooses to pay from the app.
ALTER TABLE sales ADD COLUMN payment_preference_id VARCHAR(255);

-- Result of the last payment: a rejected one keeps the sale pending and its reason
-- (the status detail of the gateway) is shown to the patient.
ALTER TABLE sales ADD COLUMN payment_status VARCHAR(20);

ALTER TABLE sales ADD COLUMN payment_status_detail VARCHAR(100);

UPDATE sales SET payment_status = 'APPROVED' WHERE payment_method IS NOT NULL;

ALTER TABLE sales ADD CONSTRAINT sales_payment_status_chk
  CHECK (payment_status IS NULL OR payment_status IN ('APPROVED', 'REJECTED'));

-- The gateway payment id: a repeated notification of the same payment is ignored.
ALTER TABLE sales ADD CONSTRAINT sales_transaction_reference_uk UNIQUE (transaction_reference);
