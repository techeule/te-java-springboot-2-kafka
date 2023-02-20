package com.techeule.examples.avro.kafka.control;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techeule.examples.avro.kafka.entity.KafkaMessage;

@Repository
public interface KafkaMessageRepository extends JpaRepository<KafkaMessage, String> {

  List<KafkaMessage> findAllByTargetTopic(final String targetTopic);
}
