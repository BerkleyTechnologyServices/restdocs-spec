package com.berkleytechnologyservices.restdocs.model;

import java.util.List;

public class OpenApiRequest {

  private String path;
  private String httpMethod;
  private String basePath;
  private String host;
  private List<OpenApiParameter> queryParameters;
  private List<OpenApiParameter> pathParameters;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public List<OpenApiParameter> getQueryParameters() {
    return queryParameters;
  }

  public void setQueryParameters(List<OpenApiParameter> queryParameters) {
    this.queryParameters = queryParameters;
  }

  public List<OpenApiParameter> getPathParameters() {
    return pathParameters;
  }

  public void setPathParameters(List<OpenApiParameter> pathParameters) {
    this.pathParameters = pathParameters;
  }

}
