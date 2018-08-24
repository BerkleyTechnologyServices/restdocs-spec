package com.berkleytechnologyservices.restdocs.mojo.postman;

import java.util.ArrayList;
import java.util.List;

public class PostmanRequest {

  private String method;
  private final List<String> header;
  private PostmanUrl url;

  public PostmanRequest() {
    this.header = new ArrayList<>();
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public List<String> getHeader() {
    return header;
  }

  public PostmanUrl getUrl() {
    return url;
  }

  public void setUrl(PostmanUrl url) {
    this.url = url;
  }
}
