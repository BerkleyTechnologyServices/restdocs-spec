package com.berkleytechnologyservices.restdocs.spec.generator.openapi_v2;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGenerator;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorUtils;
import com.epages.restdocs.apispec.model.ResourceModel;
import com.epages.restdocs.apispec.openapi2.OpenApi20Generator;

import javax.inject.Named;
import java.util.List;

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
        details.getHosts().iterator().next(),
        details.getSchemes(),
        details.getName(),
        details.getDescription(),
        SpecificationGeneratorUtils.createTagDescriptionsMap(details.getTags()),
        details.getVersion(),
        SpecificationGeneratorUtils.createOauth2Configuration(details.getAuthConfig()),
        details.getFormat().name().toLowerCase()
    );
  }

}
