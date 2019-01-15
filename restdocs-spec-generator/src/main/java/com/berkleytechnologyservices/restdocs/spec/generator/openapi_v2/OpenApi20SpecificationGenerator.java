package com.berkleytechnologyservices.restdocs.spec.generator.openapi_v2;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.AuthConfig;
import com.berkleytechnologyservices.restdocs.spec.Scope;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGenerator;
import com.epages.restdocs.apispec.model.Oauth2Configuration;
import com.epages.restdocs.apispec.model.ResourceModel;
import com.epages.restdocs.apispec.openapi2.OpenApi20Generator;

import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class OpenApi20SpecificationGenerator implements SpecificationGenerator {

  private final OpenApi20Generator generator;

  public OpenApi20SpecificationGenerator() {
    this(OpenApi20Generator.INSTANCE);
  }

  public OpenApi20SpecificationGenerator(OpenApi20Generator generator) {
    this.generator = generator;
  }

  @Override
  public Specification getSpecification() {
    return Specification.OPENAPI_V2;
  }

  @Override
  public String generate(ApiDetails details, List<ResourceModel> models) {
    return generator.generateAndSerialize(
        models,
        details.getBasePath(),
        details.getHost(),
        details.getSchemes(),
        details.getName(),
        details.getDescription(),
        details.getTagDescriptions(),
        details.getVersion(),
        createOauth2Configuration(details.getAuthConfig()),
        details.getFormat().name().toLowerCase()
    );
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
