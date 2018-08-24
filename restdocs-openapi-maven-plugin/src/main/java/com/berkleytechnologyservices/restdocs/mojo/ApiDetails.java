package com.berkleytechnologyservices.restdocs.mojo;

public class ApiDetails {

  private final String name;
  private final String version;

  public ApiDetails(String name, String version) {
    this.name = name;
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }
}
