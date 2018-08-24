package com.berkleytechnologyservices.restdocs.mojo.openapi_v3;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import com.berkleytechnologyservices.restdocs.model.OpenApiParameter;
import com.berkleytechnologyservices.restdocs.model.OpenApiRequest;
import com.berkleytechnologyservices.restdocs.model.OpenApiResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OpenApiBuilder {

  private static final Map<Class<?>, Schema> TYPE_MAP = ImmutableMap.<Class<?>, Schema>builder()
      .put(String.class, new Schema().type("string"))
      .put(Boolean.class, new Schema().type("boolean"))
      .put(Integer.class, new Schema().type("integer").format("int32"))
      .put(Long.class, new Schema().type("integer").format("int64"))
      .put(Float.class, new Schema().type("number").format("float"))
      .put(Double.class, new Schema().type("number").format("double"))
      .put(Byte.class, new Schema().type("string").format("byte"))
      .put(Date.class, new Schema().type("string").format("date-time"))
      .put(Calendar.class, new Schema().type("string").format("date-time"))
      .build();

  private final Set<String> serverUrls;
  private final Map<String, PathItem> pathItems;

  public OpenApiBuilder() {
    this.serverUrls = new HashSet<>();
    this.pathItems = new HashMap<>();
  }

  public OpenApiBuilder serverUrl(String url) {
    this.serverUrls.add(url);
    return this;
  }

  public OpenApiBuilder serverUrl(String schema, String host, String basePath) {
    return this.serverUrl(schema + "://" + host + (basePath.startsWith("/") ? basePath : "/" + basePath));
  }

  public OpenApiBuilder pathItem(String path, PathItem pathItem) {
    this.pathItems.put(path, pathItem);
    return this;
  }

  public OpenApiBuilder operation(String path, String method, Operation operation) {
    PathItem pathItem = pathItems.get(path);

    if (pathItem == null) {
      pathItem = new PathItem();
      pathItems.put(path, pathItem);
    }

    if ("get".equalsIgnoreCase(method)) {
      pathItem.get(operation);
    } else if ("post".equalsIgnoreCase(method)) {
      pathItem.post(operation);
    } else if ("put".equalsIgnoreCase(method)) {
      pathItem.put(operation);
    } else if ("delete".equalsIgnoreCase(method)) {
      pathItem.delete(operation);
    } else if ("patch".equalsIgnoreCase(method)) {
      pathItem.patch(operation);
    }
    return this;
  }

  private OpenApiBuilder model(OpenApiRequest request, OpenApiResponse response) {
    Operation operation = new Operation()
        .parameters(createParameters(request))
        .responses(new ApiResponses().addApiResponse(Integer.toString(response.getStatus()), new ApiResponse().description("success")));
    return this
        .serverUrl("http", request.getHost(), request.getBasePath())
        .operation(request.getPath(), request.getHttpMethod(), operation);
  }

  public OpenApiBuilder model(OpenApiModel model) {
    return this.model(model.getRequest(), model.getResponse());
  }

  public OpenAPI build() {
    OpenAPI openAPI = new OpenAPI()
        .info(createInfo())
        .openapi("3.0.0")
        .servers(createServerList(this.serverUrls));

    pathItems.forEach(openAPI::path);

    return openAPI;
  }

  private List<Server> createServerList(Set<String> serverPaths) {
    return serverPaths.stream().map(this::createServer).collect(Collectors.toList());
  }

  public Server createServer(String serverPath) {
    return new Server().url(serverPath);
  }

  private List<Parameter> createParameters(OpenApiRequest request) {
    List<Parameter> parameters = new ArrayList<>();
    parameters.addAll(createPathParameters(request.getPathParameters()));
    parameters.addAll(createQueryParameters(request.getQueryParameters()));
    return parameters;
  }

  private List<Parameter> createPathParameters(List<OpenApiParameter> openApiParameters) {
    return openApiParameters != null ? openApiParameters.stream().map(this::createPathParameter).collect(Collectors.toList()) : Collections.emptyList();
  }

  public Parameter createPathParameter(OpenApiParameter openApiParameter) {
    return createParameter(openApiParameter, "path");
  }

  private List<Parameter> createQueryParameters(List<OpenApiParameter> openApiParameters) {
    return openApiParameters != null ? openApiParameters.stream().map(this::createQueryParameter).collect(Collectors.toList()) : Collections.emptyList();
  }

  public Parameter createQueryParameter(OpenApiParameter openApiParameter) {
    return createParameter(openApiParameter, "query");
  }

  public Parameter createParameter(OpenApiParameter openApiParameter, String in) {
    return new Parameter()
        .name(openApiParameter.getName())
        .required(true)
        .description("The " + openApiParameter.getName() + " parameter")
        .in(in)
        .schema(createSchema(openApiParameter.getType()));
  }

  private Schema createSchema(Class<?> type) {
    return TYPE_MAP.getOrDefault(Primitives.wrap(type), new Schema().type("object"));
  }

  private Info createInfo() {
    return new Info()
        .title("MyAPI")
        .version("1.0.0");
  }
}
