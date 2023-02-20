CREATE TABLE kafka_message
(
    id              VARCHAR(255) NOT NULL,
    target_topic    VARCHAR(255),
    message_payload BYTEA,
    message_key     VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_kafkamessage PRIMARY KEY (id)
);