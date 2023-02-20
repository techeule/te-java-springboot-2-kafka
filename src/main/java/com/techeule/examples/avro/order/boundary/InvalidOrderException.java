package com.techeule.examples.avro.order.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid OrderPlacement")
public class InvalidOrderException extends RuntimeException {
}
