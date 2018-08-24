package com.berkleytechnologyservices.restdocs.mojo.postman;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import com.berkleytechnologyservices.restdocs.model.OpenApiParameter;
import com.berkleytechnologyservices.restdocs.model.OpenApiRequest;
import com.berkleytechnologyservices.restdocs.mojo.ApiDetails;
import com.berkleytechnologyservices.restdocs.mojo.Specification;
import com.berkleytechnologyservices.restdocs.mojo.SpecificationGenerator;
import com.berkleytechnologyservices.restdocs.mojo.SpecificationGeneratorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Named;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Named
public class PostmanSpecificationGenerator implements SpecificationGenerator {

  @Override
  public Specification getSpecification() {
    return Specification.POSTMAN;
  }

  @Override
  public String generate(ApiDetails details, List<OpenApiModel> models) throws SpecificationGeneratorException {
    PostmanInfo info = new PostmanInfo(
        details.getName(),
        "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    );

    PostmanCollection collection = new PostmanCollection();
    collection.setInfo(info);

    List<PostmanItem> items = models.stream()
        .map((model) -> {

          List<PostmanParam> postmanVariables = createPostmanParams(model.getRequest().getPathParameters());
          List<PostmanParam> postmanQueryParams = createPostmanParams(model.getRequest().getQueryParameters());

          String rawUrl = createItemName(model.getRequest());

          rawUrl = replacePathVariables(rawUrl, postmanVariables);

          StringJoiner joiner = new StringJoiner("&");
          postmanQueryParams.forEach((param) -> joiner.add(param.getKey() + "=" + param.getValue()));
          if (joiner.length() > 0) {
            rawUrl = rawUrl + "?" + joiner.toString();
          }

          PostmanUrl url = new PostmanUrl();
          url.setRaw(rawUrl);
          url.setProtocol("http");
          url.getHost().add(model.getRequest().getHost());
          url.setPort("80");
          url.getPath().addAll(splitPath(model.getRequest().getBasePath(), postmanVariables));
          url.getPath().addAll(splitPath(model.getRequest().getPath(), postmanVariables));
          url.getVariable().addAll(postmanVariables);
          url.getQuery().addAll(postmanQueryParams);

          PostmanRequest request = new PostmanRequest();
          request.setMethod(model.getRequest().getHttpMethod().toUpperCase());
          request.setUrl(url);

          PostmanItem item = new PostmanItem();
          item.setName(createItemName(model.getRequest()));
          item.setRequest(request);

          return item;
        })
        .collect(Collectors.toList());

    collection.getItem().addAll(items);

    try {
      return new ObjectMapper().writeValueAsString(collection);
    } catch (JsonProcessingException e) {
      throw new SpecificationGeneratorException("Unable to generate specification.", e);
    }
  }

  private String createItemName(OpenApiRequest request) {
    return "http://" + request.getHost() + request.getBasePath() + request.getPath();
  }

  private List<PostmanParam> createPostmanParams(List<OpenApiParameter> params) {
    return params != null ? params.stream().map(this::createPostmanParam).collect(Collectors.toList()) : Collections.emptyList();
  }

  private PostmanParam createPostmanParam(OpenApiParameter param) {
    PostmanParam postmanParam = new PostmanParam();
    postmanParam.setKey(param.getName());
    postmanParam.setValue("{{"+param.getName()+"}}");
    return postmanParam;
  }

  private List<String> splitPath(String path, List<PostmanParam> postmanVariables) {
    return Arrays.asList(replacePathVariables(path, postmanVariables).replaceAll("^/","").split("/"));
  }

  private String replacePathVariables(String url, List<PostmanParam> postmanVariables) {
    String updatedUrl = url;
    for (PostmanParam variable : postmanVariables) {
      updatedUrl = updatedUrl.replaceAll("\\{" + variable.getKey() + "\\}", ":" + variable.getKey());
    }
    return updatedUrl;
  }
}
