package com.berkleytechnologyservices.restdocs.snippets;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import com.berkleytechnologyservices.restdocs.model.OpenApiParameter;
import com.berkleytechnologyservices.restdocs.model.OpenApiRequest;
import com.berkleytechnologyservices.restdocs.model.OpenApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OpenApiSnippet implements Snippet {

  private static final String QUERY_DELIMITER = "?";
  private static final String RESULT_ATTR = "org.springframework.test.web.servlet.MockMvc.MVC_RESULT_ATTRIBUTE";

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
    HandlerMethod handlerMethod = (HandlerMethod) ((MvcResult) servletRequest.getAttribute(RESULT_ATTR)).getHandler();
    Map<String, Class> allParameters = Arrays.stream(handlerMethod.getMethodParameters())
                                           .collect(Collectors.toMap(MethodParameter::getParameterName, MethodParameter::getParameterType));

    Map<String, Object> pathVariables = (Map<String, Object>) servletRequest.getAttribute("org.springframework.web.servlet.View.pathVariables");
    request.setPathParameters(pathVariables != null ? getPathParameters(pathVariables) : new ArrayList<>());
    request.setQueryParameters(getQueryParameters(servletRequest.getParameterMap().keySet(), allParameters));
    request.setBasePath(servletRequest.getContextPath());
    request.setHost(operation.getRequest().getUri().getHost());
    request.setHttpMethod(operation.getRequest().getMethod().name());
    request.setPath(getMethodPath(servletRequest, request.getBasePath()));

    return request;
  }

  private String getMethodPath(MockHttpServletRequest servletRequest, String basePath) {
    String methodPath = (String) servletRequest.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingPattern");
    if (methodPath == null) {
      methodPath = servletRequest.getPathInfo();
    }
    return methodPath.replaceAll(basePath, "");
  }

  private List<OpenApiParameter> getQueryParameters(Set<String> queryParameterNames, Map<String, Class> allParameters) {
    // currently unable to correctly determine types of query parameters when using custom HandlerMethodArgumentResolver.
    // In these scenarios, just use Object.class for parmeter type
    List<OpenApiParameter> parameters = new ArrayList<>();
    for (String queryParameterName : queryParameterNames) {
      Class type = allParameters.entrySet().stream()
                       .filter(p -> p.getKey().equals(queryParameterName))
                       .findFirst()
                       .map(p -> p.getValue())
                       .orElse(Object.class);
      parameters.add(new OpenApiParameter(queryParameterName, type));
    }
    return parameters;
  }

  private List<OpenApiParameter> getPathParameters(Map<String, Object> pathVariableValues) {
    return pathVariableValues.entrySet().stream()
               .map(param -> new OpenApiParameter(param.getKey(), param.getValue().getClass()))
               .collect(Collectors.toList());
  }

}
