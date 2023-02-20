package com.techeule.examples.avro.configuration.boundary;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "_/spring-configuration-metadata", produces = "application/json")
public class SpringConfigurationMetadataResource {

  private JsonNode springConfigurationMetadata;

  private final ObjectMapper objectMapper;

  public SpringConfigurationMetadataResource(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  void postConstruct() throws IOException {
    final InputStream resourceAsStream = SpringConfigurationMetadataResource.class.getClassLoader()
                                                                                  .getResourceAsStream(
                                                                                    "META-INF/spring-configuration-metadata.json");
    springConfigurationMetadata = objectMapper.readTree(resourceAsStream);
  }

  @GetMapping(value = "/")
  public JsonNode get() {
    return springConfigurationMetadata;
  }
}
