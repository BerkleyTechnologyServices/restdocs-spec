package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.model.OpenApiParameter;
import com.berkleytechnologyservices.restdocs.model.OpenApiRequest;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  public OpenApiBuilder request(OpenApiRequest request) {
    Operation operation = new Operation();
    operation.setParameters(convertParameters(request.getPathParameters()));
    return this
        .serverUrl("http", request.getHost(), request.getBasePath())
        .operation(request.getPath(), request.getHttpMethod(), operation);
  }

  public OpenAPI build() {
    OpenAPI openAPI = new OpenAPI();
    openAPI.setInfo(new Info());

    openAPI.setOpenapi("3.0");
    openAPI.setServers(createServerList(this.serverUrls));

    for (Map.Entry<String, PathItem> entry : pathItems.entrySet()) {
      openAPI.path(entry.getKey(), entry.getValue());
    }

    return openAPI;
  }

  private List<Server> createServerList(Set<String> serverPaths) {
    List<Server> servers = new ArrayList<Server>();
    for (String serverPath : serverPaths) {
      Server server = new Server();
      server.setUrl(serverPath);
      servers.add(server);
    }
    return servers;
  }

  private List<Parameter> convertParameters(List<OpenApiParameter> openApiParameters) {
    List<Parameter> parameters = new ArrayList<>();
    for (OpenApiParameter openApiParameter : openApiParameters) {
      Parameter parameter = new Parameter();
      parameter.setName(openApiParameter.getName());
      parameter.setRequired(true);
      parameter.setDescription("The " + openApiParameter.getName() + " parameter");
      parameters.add(parameter);
    }
    return parameters;
  }
}
