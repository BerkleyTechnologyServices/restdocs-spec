package com.berkleytechnologyservices.restdocs.spec;

public class Host {
  private String value;
  private String description;

  public Host() {
  }

  public Host(String value) {
    this(value, null);
  }

  public Host(String value, String description) {
    this.value = value;
    this.description = description;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
