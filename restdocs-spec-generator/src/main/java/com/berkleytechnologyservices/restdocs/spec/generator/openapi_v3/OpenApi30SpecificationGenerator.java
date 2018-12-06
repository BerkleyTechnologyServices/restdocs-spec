package com.berkleytechnologyservices.restdocs.spec.generator.openapi_v3;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGenerator;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorUtils;
import com.epages.restdocs.apispec.model.ResourceModel;
import com.epages.restdocs.apispec.openapi3.OpenApi3Generator;
import io.swagger.v3.oas.models.servers.Server;

import javax.inject.Named;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Named
public class OpenApi30SpecificationGenerator implements SpecificationGenerator {

  private final OpenApi3Generator generator;

  public OpenApi30SpecificationGenerator() {
    this(OpenApi3Generator.INSTANCE);
  }

  public OpenApi30SpecificationGenerator(OpenApi3Generator generator) {
    this.generator = generator;
  }

  @Override
  public Specification getSpecification() {
    return Specification.OPENAPI_V3;
  }

  @Override
  public String generate(ApiDetails details, List<ResourceModel> models) {
    List<Server> servers = new ArrayList<>();
    for (String scheme : details.getSchemes()) {
      try {
        URL url = new URL(scheme, details.getHost(), details.getBasePath() == null ? "" : details.getBasePath());
        servers.add(new Server().url(url.toString()));
      } catch (MalformedURLException e) {
      	throw new IllegalArgumentException("Invalid server URL", e);
      }
    }
    return generator.generateAndSerialize(
            models,
            servers,
            details.getName(),
            details.getDescription(),
            details.getTagDescriptions(),
            details.getVersion(),
            SpecificationGeneratorUtils.createOauth2Configuration(details.getAuthConfig()),
            details.getFormat().name().toLowerCase()
    );
  }

}
