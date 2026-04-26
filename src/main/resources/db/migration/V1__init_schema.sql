CREATE TABLE IF NOT EXISTS messages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name   VARCHAR(30)  NOT NULL,
    body            TEXT         NOT NULL,
    idempotency_key VARCHAR(64)  UNIQUE,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_log (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id   BIGINT      NOT NULL,
    user_id      BIGINT      NOT NULL,
    user_name    VARCHAR(100),
    category     VARCHAR(30),
    channel      VARCHAR(30) NOT NULL,
    status       VARCHAR(10) NOT NULL,
    error_detail TEXT,
    sent_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_log_status CHECK (status IN ('SENT', 'FAILED')),
    CONSTRAINT fk_log_message FOREIGN KEY (message_id) REFERENCES messages(id)
);

CREATE INDEX idx_log_sent_at    ON notification_log(sent_at DESC);
CREATE INDEX idx_log_message_id ON notification_log(message_id);
CREATE INDEX idx_log_user_id    ON notification_log(user_id);
CREATE INDEX idx_messages_idem  ON messages(idempotency_key);
