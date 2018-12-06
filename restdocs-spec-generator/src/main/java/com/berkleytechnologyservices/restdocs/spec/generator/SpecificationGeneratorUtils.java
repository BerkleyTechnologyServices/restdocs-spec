package com.berkleytechnologyservices.restdocs.spec.generator;

import com.berkleytechnologyservices.restdocs.spec.AuthConfig;
import com.berkleytechnologyservices.restdocs.spec.Scope;
import com.epages.restdocs.apispec.model.Oauth2Configuration;

import java.util.stream.Collectors;

public class SpecificationGeneratorUtils {

  private SpecificationGeneratorUtils() {
  }

  public static Oauth2Configuration createOauth2Configuration(AuthConfig authConfig) {

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
