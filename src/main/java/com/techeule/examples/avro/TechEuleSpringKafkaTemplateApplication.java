package com.techeule.examples.avro;

import static java.lang.System.Logger.Level.INFO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechEuleSpringKafkaTemplateApplication {

  private static final System.Logger logger = System.getLogger(TechEuleSpringKafkaTemplateApplication.class.getName());

  public static void main(final String[] args) {
    logger.log(INFO, "Hello Tech Eule \uD83E\uDD89");
    SpringApplication.run(TechEuleSpringKafkaTemplateApplication.class, args);
  }
}
