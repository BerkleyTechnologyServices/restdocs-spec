package com.berkleytechnologyservices.restdocs.mojo.openapi_v3;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import com.berkleytechnologyservices.restdocs.model.OpenApiParameter;
import com.berkleytechnologyservices.restdocs.model.OpenApiRequest;
import com.berkleytechnologyservices.restdocs.model.OpenApiResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OpenApiBuilder {

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
        .parameters(createParameters(request.getPathParameters()))
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

  private List<Parameter> createParameters(List<OpenApiParameter> openApiParameters) {
    return openApiParameters.stream().map(this::createParameter).collect(Collectors.toList());
  }

  public Server createServer(String serverPath) {
    return new Server().url(serverPath);
  }

  public Parameter createParameter(OpenApiParameter openApiParameter) {
    return new Parameter()
        .name(openApiParameter.getName())
        .required(true)
        .description("The " + openApiParameter.getName() + " parameter")
        .in("path")
        .schema(new Schema().type("string"));
  }

  private Info createInfo() {
    return new Info()
        .title("My API")
        .version("1.0.0");
  }
}
