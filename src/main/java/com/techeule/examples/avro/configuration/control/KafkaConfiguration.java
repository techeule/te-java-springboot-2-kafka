package com.techeule.examples.avro.configuration.control;

import javax.validation.constraints.NotBlank;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import com.techeule.examples.avro.kafka.boundary.KafkaMessageProducer;
import com.techeule.examples.avro.kafka.control.KafkaMessageRepository;
import com.techeule.examples.avro.schemas.OrderEvent;

@Configuration
@EnableKafka
@ConfigurationProperties(prefix = "order-kafka")
public class KafkaConfiguration {
  @NotBlank
  String topicName = "order-events";

  @Bean
  KafkaMessageProducer<OrderEvent> orderEventKafkaMessageProducer(final KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate,
                                                                  final KafkaMessageRepository kafkaMessageRepository) {
    return new KafkaMessageProducer<>(orderEventKafkaTemplate,
                                      OrderEvent.class,
                                      kafkaMessageRepository,
                                      topicName,
                                      KafkaMessageProducer.Mode.SAVE_ON_ERROR);
  }

  @Bean
  public NewTopic topicOrderEvents() {
    return TopicBuilder.name(topicName)
                       .partitions(2)
                       .replicas(1)
                       .build();
  }
}
