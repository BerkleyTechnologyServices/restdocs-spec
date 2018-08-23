package com.berkleytechnologyservices.restdocs.model;

public class OpenApiModel {
  
  private OpenApiRequest request;
  private OpenApiResponse response;

  public OpenApiRequest getRequest() {
    return request;
  }

  public void setRequest(OpenApiRequest request) {
    this.request = request;
  }

  public OpenApiResponse getResponse() {
    return response;
  }

  public void setResponse(OpenApiResponse response) {
    this.response = response;
  }
}
