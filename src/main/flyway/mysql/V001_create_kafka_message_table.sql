CREATE TABLE kafka_message
(
    id              VARCHAR(255) NOT NULL,
    target_topic    VARCHAR(255) NULL,
    message_payload BLOB         NULL,
    message_key     VARCHAR(255) NULL,
    created_at      datetime     NULL,
    CONSTRAINT pk_kafkamessage PRIMARY KEY (id)
);