package com.berkleytechnologyservices.restdocs.spec;

public enum SpecificationFormat {
  YAML(".yml"),
  JSON(".json");

  private final String extension;

  SpecificationFormat(String extension) {
    this.extension = extension;
  }

  public String getExtension() {
    return extension;
  }
}
