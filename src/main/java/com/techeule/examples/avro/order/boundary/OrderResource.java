package com.techeule.examples.avro.order.boundary;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techeule.examples.avro.order.entity.OrderPlacement;

@Component
@Scope(SCOPE_SINGLETON)
@RestController
@RequestMapping(value = "/orders", consumes = "application/json", produces = "application/json")
public class OrderResource {

  private final OrderPlacementProcessor orderPlacementProcessor;

  public OrderResource(final OrderPlacementProcessor orderPlacementProcessor) {
    this.orderPlacementProcessor = orderPlacementProcessor;
  }

  @PostMapping
  public Map<String, Object> postOrder(@RequestBody final OrderPlacement orderPlacement) {
    final var orderId = orderPlacementProcessor.process(orderPlacement);
    return Map.of("orderId", orderId);
  }
}
