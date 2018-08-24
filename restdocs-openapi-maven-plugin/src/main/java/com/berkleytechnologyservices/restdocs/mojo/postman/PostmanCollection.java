package com.berkleytechnologyservices.restdocs.mojo.postman;

import java.util.ArrayList;
import java.util.List;

public class PostmanCollection {

  private PostmanInfo info;
  private final List<PostmanItem> item;

  public PostmanCollection() {
    this.item = new ArrayList<>();
  }

  public PostmanInfo getInfo() {
    return info;
  }

  public void setInfo(PostmanInfo info) {
    this.info = info;
  }

  public List<PostmanItem> getItem() {
    return item;
  }
}
