package com.berkleytechnologyservices.restdocs.spec.generator.openapi_v3;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGenerator;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorException;
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
  public String generate(ApiDetails details, List<ResourceModel> models) throws SpecificationGeneratorException {
    return generator.generateAndSerialize(
            models,
            createServerList(details),
            details.getName(),
            details.getDescription(),
            SpecificationGeneratorUtils.createTagDescriptionsMap(details.getTags()),
            details.getVersion(),
            SpecificationGeneratorUtils.createOauth2Configuration(details.getAuthConfig()),
            details.getFormat().name().toLowerCase()
    );
  }

  private List<Server> createServerList(ApiDetails details) throws SpecificationGeneratorException {
    List<Server> servers = new ArrayList<>();
    for (String scheme : details.getSchemes()) {
      try {
        URL url = SpecificationGeneratorUtils.createBaseUrl(scheme, details.getHost(), details.getBasePath() == null ? "" : details.getBasePath());
        servers.add(new Server().url(url.toString()));
      } catch (MalformedURLException e) {
        throw new SpecificationGeneratorException("Unable to build server url.", e);
      }
    }
    return servers;
  }
}
