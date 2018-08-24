package com.berkleytechnologyservices.restdocs.mojo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class SpecificationGeneratorFactory {

  private final List<SpecificationGenerator> generators;

  @Inject
  public SpecificationGeneratorFactory(List<SpecificationGenerator> generators) {
    this.generators = generators;
  }

  public SpecificationGenerator createGenerator(Specification specification) {
    return this.generators.stream()
        .filter((generator) -> specification.equals(generator.getSpecification()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Specification is not supported: " + specification));
  }
}
