package com.techeule.examples.avro.order.boundary;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.techeule.examples.avro.kafka.boundary.KafkaMessageProducer;
import com.techeule.examples.avro.order.entity.OrderPlacement;
import com.techeule.examples.avro.schemas.Cause;
import com.techeule.examples.avro.schemas.OrderEvent;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OrderPlacementProcessor {

  private final KafkaMessageProducer<OrderEvent> orderEventKafkaMessageProducer;

  public OrderPlacementProcessor(final KafkaMessageProducer<OrderEvent> orderEventKafkaMessageProducer) {
    this.orderEventKafkaMessageProducer = orderEventKafkaMessageProducer;
  }

  public String process(final OrderPlacement orderPlacement) {
    checkOrderPlacement(orderPlacement);
    final var orderId = UUID.randomUUID().toString();
    // some business rules and logic ...
    notifyNewOrderPlacement(orderPlacement, orderId);
    return orderId;
  }

  private static void checkOrderPlacement(final OrderPlacement orderPlacement) {
    if (orderPlacement == null) {
      throw new InvalidOrderException();
    }

    if ((orderPlacement.customerId() == null) || orderPlacement.customerId().isBlank()) {
      throw new InvalidOrderException();
    }

    if ((orderPlacement.productToCount() == null) || orderPlacement.productToCount().isEmpty()) {
      throw new InvalidOrderException();
    }
  }

  private void notifyNewOrderPlacement(final OrderPlacement orderPlacement,
                         final String orderId) {
    // let other services know about an order-placement.
    final var orderEvent = new OrderEvent(orderId, orderPlacement.customerId(), Cause.CREATED, Instant.now().getEpochSecond());
    orderEventKafkaMessageProducer.send(orderEvent.getOrderId(), orderEvent);
  }
}
