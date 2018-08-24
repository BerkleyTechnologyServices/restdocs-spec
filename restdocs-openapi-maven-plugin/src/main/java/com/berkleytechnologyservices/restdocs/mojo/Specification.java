package com.berkleytechnologyservices.restdocs.mojo;

public enum Specification {
  OPENAPI_V3(".yml"),
  POSTMAN(".json");

  private final String extension;

  Specification(String extension) {
    this.extension = extension;
  }

  public String getExtension() {
    return extension;
  }
}
