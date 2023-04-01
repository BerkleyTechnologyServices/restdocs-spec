package com.berkleytechnologyservices.restdocs.spec.generator;

import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.generator.openapi_v2.OpenApi20SpecificationGenerator;
import com.berkleytechnologyservices.restdocs.spec.generator.openapi_v3.OpenApi30SpecificationGenerator;
import com.berkleytechnologyservices.restdocs.spec.generator.postman.PostmanCollectionSpecificationGenerator;

import javax.inject.Named;
import javax.inject.Inject;
import java.util.List;

@Named
public class SpecificationGeneratorFactory {

  private final List<SpecificationGenerator> generators;


  @Inject
  public SpecificationGeneratorFactory(
          OpenApi20SpecificationGenerator openApi20SpecificationGenerator,
          OpenApi30SpecificationGenerator openApi30SpecificationGenerator,
          PostmanCollectionSpecificationGenerator postmanCollectionSpecificationGenerator
  ) {
    this.generators = List.of(
            openApi20SpecificationGenerator,
            openApi30SpecificationGenerator,
            postmanCollectionSpecificationGenerator
    );
  }

  public SpecificationGenerator createGenerator(Specification specification) {
    return this.generators.stream()
        .filter((generator) -> specification.equals(generator.getSpecification()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Specification is not supported: " + specification));
  }
}
