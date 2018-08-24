package com.berkleytechnologyservices.restdocs.mojo.openapi_v3;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import com.berkleytechnologyservices.restdocs.mojo.ApiDetails;
import com.berkleytechnologyservices.restdocs.mojo.Specification;
import com.berkleytechnologyservices.restdocs.mojo.SpecificationGenerator;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;

import javax.inject.Named;
import java.util.List;

@Named
public class OpenApiSpecificationGenerator implements SpecificationGenerator {

  @Override
  public Specification getSpecification() {
    return Specification.OPENAPI_V3;
  }

  @Override
  public String generate(ApiDetails details, List<OpenApiModel> models) {
    OpenApiBuilder builder = new OpenApiBuilder()
        .name(details.getName())
        .version(details.getVersion());

    models.forEach(builder::model);

    OpenAPI openAPI = builder.build();

    return Yaml.pretty(openAPI);
  }
}
