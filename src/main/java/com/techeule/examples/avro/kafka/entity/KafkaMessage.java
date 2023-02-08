package com.techeule.examples.avro.kafka.entity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class KafkaMessage {
    @Id
    private String id;
    private String targetTopic;
    private byte[] messagePayload;
    private String messageKey;
    private LocalDateTime createdAt;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final KafkaMessage that)) {
            return false;
        }
        return Objects.equals(targetTopic, that.targetTopic) && Arrays.equals(messagePayload,
                                                                              that.messagePayload) && Objects.equals(
                messageKey,
                that.messageKey);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(targetTopic, messageKey);
        result = (31 * result) + Arrays.hashCode(messagePayload);
        return result;
    }
}
