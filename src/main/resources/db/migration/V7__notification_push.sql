CREATE TABLE device_tokens (
  id UUID PRIMARY KEY,
  patient_id UUID NOT NULL,
  token VARCHAR(512) NOT NULL,
  platform VARCHAR(20) NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT device_tokens_patient_fk FOREIGN KEY (patient_id) REFERENCES patients (id),
  CONSTRAINT device_tokens_token_uk UNIQUE (token),
  CONSTRAINT device_tokens_platform_chk CHECK (platform IN ('ANDROID'))
);

CREATE INDEX device_tokens_patient_idx ON device_tokens (patient_id);

-- Notifications created before V7 were never sent as push notifications.
ALTER TABLE notifications ADD COLUMN push_status VARCHAR(10);
UPDATE notifications SET push_status = 'FAILED';
ALTER TABLE notifications ALTER COLUMN push_status SET NOT NULL;
ALTER TABLE notifications ADD COLUMN scheduled_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE notifications ADD COLUMN survey_rating INT;
ALTER TABLE notifications ADD COLUMN survey_comment VARCHAR(500);
ALTER TABLE notifications ADD COLUMN answered_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE notifications ADD CONSTRAINT notifications_push_status_chk
  CHECK (push_status IN ('PENDING', 'SENT', 'FAILED'));
ALTER TABLE notifications ADD CONSTRAINT notifications_survey_rating_chk
  CHECK (survey_rating BETWEEN 1 AND 5);

ALTER TABLE notifications DROP CONSTRAINT notifications_type_chk;
ALTER TABLE notifications ADD CONSTRAINT notifications_type_chk CHECK (type IN (
  'APPOINTMENT_REMINDER',
  'ANNUAL_CHECKUP_REMINDER',
  'LENS_ORDER_PROGRESS',
  'BIRTHDAY_DISCOUNT',
  'DELIVERY_DELAY',
  'SATISFACTION_SURVEY'
));

-- One reminder of each type per appointment.
DROP INDEX notifications_patient_appointment_uq;
CREATE UNIQUE INDEX notifications_patient_appointment_type_uq
  ON notifications (patient_id, appointment_id, type)
  WHERE appointment_id IS NOT NULL;

CREATE INDEX notifications_push_pending_idx ON notifications (push_status, scheduled_at);
