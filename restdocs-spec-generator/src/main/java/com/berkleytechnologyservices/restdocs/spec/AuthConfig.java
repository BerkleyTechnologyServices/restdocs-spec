package com.berkleytechnologyservices.restdocs.spec;

import java.util.Collections;
import java.util.List;

public class AuthConfig {

  private String tokenUrl;
  private String authorizationUrl;
  private List<String> flows;
  private List<Scope> scopes;

  public AuthConfig() {
    this.flows = Collections.emptyList();
    this.scopes = Collections.emptyList();
  }

  public String getTokenUrl() {
    return tokenUrl;
  }

  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }

  public String getAuthorizationUrl() {
    return authorizationUrl;
  }

  public void setAuthorizationUrl(String authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
  }

  public List<String> getFlows() {
    return flows;
  }

  public void setFlows(List<String> flows) {
    this.flows = flows != null ? Collections.unmodifiableList(flows) : Collections.emptyList();
  }

  public List<Scope> getScopes() {
    return scopes;
  }

  public void setScopes(List<Scope> scopes) {
    this.scopes = scopes != null ? Collections.unmodifiableList(scopes) : Collections.emptyList();
  }
}
