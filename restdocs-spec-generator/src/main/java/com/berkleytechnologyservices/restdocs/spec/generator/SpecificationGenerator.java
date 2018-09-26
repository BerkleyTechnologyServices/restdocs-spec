package com.berkleytechnologyservices.restdocs.spec.generator;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.epages.restdocs.openapi.model.ResourceModel;

import java.util.List;

public interface SpecificationGenerator {
  Specification getSpecification();
  String generate(ApiDetails details, List<ResourceModel> models) throws SpecificationGeneratorException;
}
