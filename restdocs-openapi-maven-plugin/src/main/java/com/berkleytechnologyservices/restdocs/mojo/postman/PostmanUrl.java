package com.berkleytechnologyservices.restdocs.mojo.postman;

import java.util.ArrayList;
import java.util.List;

public class PostmanUrl {

  private String raw;
  private String protocol;
  private final List<String> host;
  private String port;
  private final List<String> path;
  private final List<PostmanParam> query;
  private final List<PostmanParam> variable;

  public PostmanUrl() {
    this.host = new ArrayList<>();
    this.path = new ArrayList<>();
    this.query = new ArrayList<>();
    this.variable = new ArrayList<>();
  }

  public String getRaw() {
    return raw;
  }

  public void setRaw(String raw) {
    this.raw = raw;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public List<String> getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public List<String> getPath() {
    return path;
  }

  public List<PostmanParam> getQuery() {
    return query;
  }

  public List<PostmanParam> getVariable() {
    return variable;
  }
}
