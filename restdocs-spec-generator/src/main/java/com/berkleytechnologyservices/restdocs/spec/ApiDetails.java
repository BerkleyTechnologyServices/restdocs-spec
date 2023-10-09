package com.berkleytechnologyservices.restdocs.spec;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ApiDetails {

  private static final String DEFAULT_NAME = "API Documentation";
  private static final String DEFAULT_VERSION = "1.0.0";
  private static final String DEFAULT_HOST = "localhost";
  private static final List<String> DEFAULT_SCHEMES = Collections.singletonList("http");
  private static final SpecificationFormat DEFAULT_FORMAT = SpecificationFormat.YAML;

  private String name;
  private String version;
  private String description;
  private Set<String> hosts;
  private String basePath;
  private List<String> schemes;
  private SpecificationFormat format;
  private AuthConfig authConfig;
  private List<Tag> tags;

  private Contact contact;
  
  public ApiDetails() {
    this.name = DEFAULT_NAME;
    this.version = DEFAULT_VERSION;
    this.hosts = Collections.singleton(DEFAULT_HOST);
    this.basePath = null;
    this.schemes = DEFAULT_SCHEMES;
    this.format = DEFAULT_FORMAT;
    this.authConfig = new AuthConfig();
    this.tags = Collections.emptyList();
    this.contact = null;
  }

  public String getName() {
    return name;
  }

  public ApiDetails name(String name) {
    this.name = name != null ? name : DEFAULT_NAME;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public ApiDetails version(String version) {
    this.version = version != null ? version : DEFAULT_VERSION;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public ApiDetails description(String description){
    this.description = description;
    return this;
  }

  public Set<String> getHosts() {
    return hosts;
  }

  public String getHost() {
    return getHosts().stream().findFirst().orElseThrow();
  }

  public ApiDetails hosts(Set<String> hosts) {
    this.hosts = (hosts != null && !hosts.isEmpty()) ? hosts : Collections.singleton(DEFAULT_HOST);
    return this;
  }

  public String getBasePath() {
    return basePath;
  }

  public ApiDetails basePath(String basePath) {
    this.basePath = basePath;
    return this;
  }

  public List<String> getSchemes() {
    return schemes;
  }

  public ApiDetails schemes(List<String> schemes) {
    this.schemes = schemes != null && !schemes.isEmpty() ? Collections.unmodifiableList(schemes) : DEFAULT_SCHEMES;
    return this;
  }

  public SpecificationFormat getFormat() {
    return format;
  }

  public ApiDetails format(SpecificationFormat format) {
    this.format = format != null ? format : DEFAULT_FORMAT;
    return this;
  }

  public AuthConfig getAuthConfig() {
    return this.authConfig;
  }

  public ApiDetails authConfig(AuthConfig authConfig) {
    this.authConfig = authConfig != null ? authConfig : new AuthConfig();
    return this;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public ApiDetails tags(List<Tag> tags) {
    this.tags = tags != null ? tags : this.tags;
    return this;
  }

  public Optional<Contact> getContact() {
    return Optional.ofNullable(contact);
  }

  public ApiDetails contact(Contact contact) {
    this.contact = contact;
    return this;
  }
}
