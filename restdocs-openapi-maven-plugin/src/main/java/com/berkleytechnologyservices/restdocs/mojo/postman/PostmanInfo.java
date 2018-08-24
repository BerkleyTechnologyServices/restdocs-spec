package com.berkleytechnologyservices.restdocs.mojo.postman;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostmanInfo {

  private final String name;
  private final String schema;

  @JsonCreator
  public PostmanInfo(@JsonProperty("name") String name,
                     @JsonProperty("schema") String schema) {

    this.name = name;
    this.schema = schema;
  }

  public String getName() {
    return name;
  }

  public String getSchema() {
    return schema;
  }
}
