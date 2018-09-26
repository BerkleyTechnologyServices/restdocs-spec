package com.berkleytechnologyservices.restdocs.spec.generator.openapi_v2;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.AuthConfig;
import com.berkleytechnologyservices.restdocs.spec.Scope;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.SpecificationFormat;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGenerator;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorException;
import com.epages.restdocs.openapi.generator.OpenApi20Generator;
import com.epages.restdocs.openapi.model.Oauth2Configuration;
import com.epages.restdocs.openapi.model.ResourceModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
public class OpenApi20SpecificationGenerator implements SpecificationGenerator {

  private static final JsonProcessingFunction YAML_GENERATOR = swagger -> Yaml.pretty().writeValueAsString(swagger);
  private static final JsonProcessingFunction JSON_GENERATOR = swagger -> Json.pretty().writeValueAsString(swagger);

  private static final Map<SpecificationFormat, JsonProcessingFunction> FORMAT_GENERATORS = createFormatGeneratorsMap();

  @Override
  public Specification getSpecification() {
    return Specification.OPENAPI_V2;
  }

  @Override
  public String generate(ApiDetails details, List<ResourceModel> models) throws SpecificationGeneratorException {
    Swagger spec = OpenApi20Generator.INSTANCE.generate(
        models,
        details.getBasePath(),
        details.getHost(),
        details.getSchemes(),
        details.getName(),
        details.getVersion(),
        createOauth2Configuration(details.getAuthConfig())
    );

    try {
      return FORMAT_GENERATORS.getOrDefault(details.getFormat(), YAML_GENERATOR).apply(spec);
    } catch (JsonProcessingException e) {
      throw new SpecificationGeneratorException("Unable to generate specification.", e);
    }
  }

  @FunctionalInterface
  interface JsonProcessingFunction {
    String apply(Swagger t) throws JsonProcessingException;
  }

  private static Map<SpecificationFormat, JsonProcessingFunction> createFormatGeneratorsMap() {
    Map<SpecificationFormat, JsonProcessingFunction> generators = new HashMap<>();
    generators.put(SpecificationFormat.YAML, YAML_GENERATOR);
    generators.put(SpecificationFormat.JSON, JSON_GENERATOR);
    return generators;
  }

  private static Oauth2Configuration createOauth2Configuration(AuthConfig authConfig) {

    Oauth2Configuration oauth2Configuration = null;

    if (authConfig.getTokenUrl() != null && authConfig.getAuthorizationUrl() != null) {
      oauth2Configuration = new Oauth2Configuration();
      oauth2Configuration.setTokenUrl(authConfig.getTokenUrl());
      oauth2Configuration.setAuthorizationUrl(authConfig.getAuthorizationUrl());
      oauth2Configuration.setFlows(authConfig.getFlows().toArray(new String[0]));

      if (!authConfig.getScopes().isEmpty()) {
        oauth2Configuration.setScopes(
            authConfig.getScopes().stream()
                .collect(Collectors.toMap(Scope::getName, Scope::getDescription))
        );
      }
    }

    return oauth2Configuration;
  }
}
