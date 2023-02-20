package com.techeule.examples.avro.order.entity;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderPlacement(
  @JsonProperty("customerId")
  String customerId,
  @JsonProperty("productToCount")
  Map<String, Integer> productToCount) {
}
