package com.berkleytechnologyservices.restdocs.mojo.postman;

import java.util.ArrayList;
import java.util.List;

public class PostmanItem {

  private String name;
  private PostmanRequest request;
  private final List<PostmanResponse> response;

  public PostmanItem() {
    this.response = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PostmanRequest getRequest() {
    return request;
  }

  public void setRequest(PostmanRequest request) {
    this.request = request;
  }

  public List<PostmanResponse> getResponse() {
    return response;
  }
}
