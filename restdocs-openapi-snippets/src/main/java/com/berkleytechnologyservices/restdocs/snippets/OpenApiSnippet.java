package com.berkleytechnologyservices.restdocs.snippets;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import com.berkleytechnologyservices.restdocs.model.OpenApiParameter;
import com.berkleytechnologyservices.restdocs.model.OpenApiRequest;
import com.berkleytechnologyservices.restdocs.model.OpenApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.WriterResolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class OpenApiSnippet implements Snippet {

  private static final String QUERY_DELIMITER = "?";

  @Override
  public void document(Operation operation) throws IOException {
    RestDocumentationContext context = (RestDocumentationContext) operation.getAttributes().get(RestDocumentationContext.class.getName());
    WriterResolver writerResolver = (WriterResolver) operation.getAttributes().get(WriterResolver.class.getName());

    OpenApiModel model = new OpenApiModel();
    model.setRequest(buildRequestAttributes(operation));
    model.setResponse(buildResponseAttributes(operation));

    String output = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(model);

    String outputDirectory = context.getOutputDirectory() + "/openapi";
    File directory = new File(outputDirectory);
    if (!directory.exists()) {
      directory.mkdir();
    }

    try (FileWriter fw = new FileWriter(getFileName(outputDirectory, context))) {
      fw.write(output);
    }

  }

  private String getFileName(String directoryPath, RestDocumentationContext context) {
    File outputDir = context.getOutputDirectory();
    String testPath = lowerCamel(context.getTestClass().getSimpleName());
    String testMethod = lowerCamel(context.getTestMethodName());
    return directoryPath + "/" + testPath + "." + testMethod + ".json";
  }

  private String lowerCamel(String str) {
    return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
  }

  private OpenApiResponse buildResponseAttributes(Operation operation) {
    OpenApiResponse response = new OpenApiResponse();
    response.setStatus(operation.getResponse().getStatus().value());
    return response;
  }

  private OpenApiRequest buildRequestAttributes(Operation operation) {
    OpenApiRequest request = new OpenApiRequest();
    MockHttpServletRequest servletRequest = (MockHttpServletRequest) operation.getAttributes().get(MockHttpServletRequest.class.getName());
    String urlTemplate = (String) operation.getAttributes().get("org.springframework.restdocs.urlTemplate");
    final String methodPath;
    if (urlTemplate != null) {
      int requestParamIndex = urlTemplate.indexOf(QUERY_DELIMITER) >= 0 ? urlTemplate.indexOf(QUERY_DELIMITER) : urlTemplate.length();
      methodPath = urlTemplate.substring(0, requestParamIndex);
    } else {
      methodPath = servletRequest.getPathInfo();
    }

    Map<String, Object> pathVariables = (Map<String, Object>) servletRequest.getAttribute("org.springframework.web.servlet.View.pathVariables");
    request.setPathParameters(pathVariables != null ? getPathParameters(pathVariables) : new ArrayList<>());

    // TODO : Infer types of query params
    request.setQueryParameters(servletRequest.getParameterMap().keySet().stream()
                                   .map(p -> new OpenApiParameter(p, Object.class))
                                   .collect(Collectors.toList()));
    request.setBasePath(servletRequest.getContextPath());
    request.setHost(operation.getRequest().getUri().getHost());
    request.setHttpMethod(operation.getRequest().getMethod().name());
    request.setPath(methodPath.replaceAll(request.getBasePath(), ""));

    return request;
  }

  private List<OpenApiParameter> getPathParameters(Map<String, Object> pathVariableValues) {
    return pathVariableValues.entrySet().stream()
               .map(param -> new OpenApiParameter(param.getKey(), param.getValue().getClass()))
               .collect(Collectors.toList());
  }

}
