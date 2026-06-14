CREATE TABLE fcm_token (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    token       VARCHAR(512) NOT NULL UNIQUE,
    device_id   VARCHAR(128),
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_fcm_token_user ON fcm_token(user_id);

ALTER TABLE book ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE notification ADD COLUMN dedup_key VARCHAR(200);
CREATE INDEX idx_notification_user_dedup ON notification(user_id, dedup_key);
