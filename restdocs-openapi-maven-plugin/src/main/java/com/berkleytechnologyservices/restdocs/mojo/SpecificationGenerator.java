package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;

import java.util.List;

public interface SpecificationGenerator {
  Specification getSpecification();
  String generate(List<OpenApiModel> models);
}
