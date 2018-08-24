package com.berkleytechnologyservices.restdocs.model;

public class OpenApiParameter {

  private String name;
  private Class type;

  public OpenApiParameter() {

  }

  public OpenApiParameter(String name, Class type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Class getType() {
    return type;
  }

  public void setType(Class type) {
    this.type = type;
  }
}
