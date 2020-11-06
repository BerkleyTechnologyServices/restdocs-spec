package com.berkleytechnologyservices.restdocs.spec.generator;

import com.berkleytechnologyservices.restdocs.spec.AuthConfig;
import com.berkleytechnologyservices.restdocs.spec.Scope;
import com.berkleytechnologyservices.restdocs.spec.Tag;
import com.epages.restdocs.apispec.model.Oauth2Configuration;
import com.google.common.base.Strings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
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

  public static Map<String, String> createTagDescriptionsMap(List<Tag> tags) {
    return tags.stream()
        .collect(Collectors.toMap(Tag::getName, Tag::getDescription));
  }

  public static URL createBaseUrl(String scheme, String host, String basePath) throws MalformedURLException {
    URL url;
    int indexOfColon = host.indexOf(':');
    if (indexOfColon > -1 && indexOfColon + 1 < host.length()) {
      url = new URL(scheme, host.substring(0, indexOfColon), Integer.parseInt(host.substring(indexOfColon + 1)), Strings.nullToEmpty(basePath));
    } else {
      url = new URL(scheme, host, Strings.nullToEmpty(basePath));
    }
    return url;
  }
}
