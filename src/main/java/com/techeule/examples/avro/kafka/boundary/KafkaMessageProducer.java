package com.techeule.examples.avro.kafka.boundary;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.techeule.examples.avro.kafka.control.AvroToBytesSerializerDeserializer;
import com.techeule.examples.avro.kafka.control.KafkaMessageRepository;
import com.techeule.examples.avro.kafka.entity.KafkaMessage;

public class KafkaMessageProducer<T extends GenericRecord> {
  private static final System.Logger logger = System.getLogger(KafkaMessageProducer.class.getName());

  private final KafkaTemplate<String, T> keyValueKafkaTemplate;
  private final KafkaMessageRepository kafkaMessageRepository;
  private final Mode mode;
  private final String topicName;
  private final AvroToBytesSerializerDeserializer<T> serializerDeserializer;

  public KafkaMessageProducer(final KafkaTemplate<String, T> keyValueKafkaTemplate,
                              final Class<T> tValueClass,
                              final KafkaMessageRepository kafkaMessageRepository,
                              final String topicName,
                              final Mode mode) {
    this.keyValueKafkaTemplate = keyValueKafkaTemplate;
    this.topicName = topicName;
    this.kafkaMessageRepository = kafkaMessageRepository;
    serializerDeserializer = new AvroToBytesSerializerDeserializer<>(tValueClass);
    this.mode = mode;
  }

  public void send(final String key,
                   final T message) {
    if (mode == Mode.SAVE_BEFORE_SEND) {
      saveThenSend(key, message);
    } else if (mode == Mode.SAVE_ON_ERROR) {
      sendAndSaveOnError(key, message);
    }
  }

  private void saveThenSend(final String key,
                            final T message) {
    final var messageId = saveMessage(key, message);
    keyValueKafkaTemplate.send(topicName, message).addCallback(
      result -> kafkaMessageRepository.deleteById(messageId),
      throwable -> logger.log(ERROR, "Can't send message to topic {0}", topicName, throwable)
    );
  }

  private void sendAndSaveOnError(final String key,
                                  final T message) {
    keyValueKafkaTemplate.send(topicName, message)
                         .addCallback(
                           result -> logger.log(DEBUG, "Successfully send message"),
                           throwable -> saveMessage(key, message)
                         );
  }

  private String saveMessage(final String key,
                             final T message) {
    final var kafkaMessage = KafkaMessage.builder()
                                         .id(UUID.randomUUID().toString())
                                         .createdAt(LocalDateTime.now())
                                         .targetTopic(topicName)
                                         .messageKey(key)
                                         .messagePayload(serializerDeserializer.serialize(List.of(message)))
                                         .build();

    final var savedMessage = kafkaMessageRepository.save(kafkaMessage);

    return savedMessage.getId();
  }

  @Scheduled(fixedDelayString = "${te.kafka.avro.message-producer.fixedDelayMs:60000}")
  public void reSend() {
    final List<KafkaMessage> kafkaMessages = kafkaMessageRepository.findAllByTargetTopic(topicName);
    kafkaMessages.forEach(this::reSend);
  }

  private void reSend(final KafkaMessage kafkaMessage) {
    final var withoutError = resendAllFromPayload(kafkaMessage);
    if (withoutError) {
      kafkaMessageRepository.deleteById(kafkaMessage.getId());
    }
  }

  private boolean resendAllFromPayload(final KafkaMessage kafkaMessage) {
    final var withError = new AtomicBoolean(false);
    final var payload = serializerDeserializer.deserialize(kafkaMessage.getMessagePayload());
    payload.forEach(message -> sendAndReportError(kafkaMessage, withError, message));
    return !withError.get();
  }

  private void sendAndReportError(final KafkaMessage kafkaMessage,
                                  final AtomicBoolean withError,
                                  final T km) {
    keyValueKafkaTemplate.send(kafkaMessage.getTargetTopic(), kafkaMessage.getMessageKey(), km)
                         .addCallback(
                           result -> logger.log(DEBUG, "Sent message to topic {0}",
                                                kafkaMessage.getTargetTopic()),
                           throwable -> withError.set(true)
                         );
  }

  public enum Mode {
    SAVE_BEFORE_SEND,
    SAVE_ON_ERROR
  }
}
